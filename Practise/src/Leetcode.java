import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import java.util.*;

class Solution {

    List<Integer>[] adj;
    int[] down;
    int[] up;

    public int[] finalPermissions(int n, int[][] edges, int[] permissions) {
        adj = new List[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();

        for (int[] e : edges) {
            adj[e[0]].add(e[1]);
            adj[e[1]].add(e[0]);
        }

        down = new int[n];
        up = new int[n];

        // Copy initial permissions
        for (int i = 0; i < n; i++) {
            down[i] = permissions[i];
        }

        // Downward pass
        dfsDown(0, -1);

        // Initialize up permissions to all bits set
        Arrays.fill(up, ~0);

        // Upward pass
        dfsUp(0, -1);

        // Final permissions
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = down[i] & up[i];
        }

        return result;
    }

    private void dfsDown(int curr, int parent) {
        for (int next : adj[curr]) {
            if (next == parent) continue;

            down[next] |= down[curr];
            dfsDown(next, curr);
        }
    }

    private void dfsUp(int curr, int parent) {
        int combined = ~0;
        for (int next : adj[curr]) {
            if (next == parent) continue;
            combined &= down[next];
        }

        for (int next : adj[curr]) {
            if (next == parent) continue;

            up[next] = up[curr] & (combined == ~0 ? down[curr] : combined);
            dfsUp(next, curr);
        }
    }


}
