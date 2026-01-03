import java.util.*;

public class MaxPathQualityNoGlobals {
    // Public API: no class-level globals used
    @SuppressWarnings("unchecked")
    public static int maxPathQuality(int n, int[] values, int[][] edges, int maxTime) {
        // Build adjacency list locally
        List<int[]>[] adj = new ArrayList[n];
        for (int i = 0; i < n; ++i) adj[i] = new ArrayList<>();
        for (int[] e : edges) {
            int u = e[0], v = e[1], t = e[2];
            adj[u].add(new int[] {v, t});
            adj[v].add(new int[] {u, t});
        }

        // memo[node][mask] = minimal time to reach (node, mask)
        int[][] memo = new int[n][1 << n];
        for (int i = 0; i < n; ++i) Arrays.fill(memo[i], Integer.MAX_VALUE);

        // Use a single-element array to hold best so it can be mutated inside dfs.
        int[] bestRef = new int[1];
        bestRef[0] = values[0]; // starting at node 0 collects its value

        // initial state
        int startMask = 1; // node 0 visited
        dfs(0, 0, startMask, values[0], maxTime, values, adj, memo, bestRef);

        return bestRef[0];
    }

    // Helper DFS: all state passed as parameters (no reliance on class fields)
    private static void dfs(int node,
                            int timeSoFar,
                            int mask,
                            int collectedValue,
                            int maxTime,
                            int[] values,
                            List<int[]>[] adj,
                            int[][] memo,
                            int[] bestRef) {
        // prune by time limit
        if (timeSoFar > maxTime) return;

        // update best (we can finish anywhere)
        if (collectedValue > bestRef[0]) bestRef[0] = collectedValue;

        // prune by memo: if we've been here with <= time, no need to continue
        if (timeSoFar >= memo[node][mask]) return;
        memo[node][mask] = timeSoFar;

        // explore neighbors
        for (int[] e : adj[node]) {
            int nei = e[0];
            int t = e[1];
            int newTime = timeSoFar + t;
            if (newTime > maxTime) continue;

            int newMask = mask;
            int newCollected = collectedValue;
            if ((mask & (1 << nei)) == 0) {
                newMask |= (1 << nei);
                newCollected += values[nei];
            }

            dfs(nei, newTime, newMask, newCollected, maxTime, values, adj, memo, bestRef);
        }
    }

    // quick test
    public static void main(String[] args) {
        int n = 4;
        int[] values = {0, 32, 10, 43};
        int[][] edges = {
            {0,1,10},
            {1,2,11},
            {0,3,14},
            {2,3,9}
        };
        int maxTime = 30;
        System.out.println(maxPathQuality(n, values, edges, maxTime)); // expected 75
    }
}
