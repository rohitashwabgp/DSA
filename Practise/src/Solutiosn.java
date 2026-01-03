/*
Binary Tree Maximum Path Sum Variant text
Problem: Given binary tree (not necessarily BST), each node has value.
Phase 1 (Down): Maximum path sum starting at node going downward.
Phase 2 (Up): Maximum path sum ending at node coming upward.
Return: For each node, max(down[node], up[node]).

Input: n nodes, parent array (not edges), values
Special: Must handle negative values, empty paths.
*/

import java.util.*;

/**
 * Solution for:
 * Given tree with parent[] and val[], compute for every node:
 * down[node] = max path sum starting at node and going downward (empty allowed)
 * up[node]   = max path sum ending at node and coming upward (empty allowed)
 * Return answer[i] = max(down[i], up[i]).
 * <p>
 * 0-based node indices.
 */
public class Solutiosn {
    /**
     * @param n      number of nodes
     * @param parent parent[i] is parent of i, parent[root] = -1
     * @param val    val[i] is value of node i
     * @return answer array where answer[i] = max(down[i], up[i])
     */
    public int[] maxDownUpPathSums(int n, int[] parent, int[] val) {
        // Build children adjacency
        List<Integer>[] children = new ArrayList[n];
        for (int i = 0; i < n; ++i) children[i] = new ArrayList<>();
        int root = -1;
        for (int i = 0; i < n; ++i) {
            if (parent[i] == -1) root = i;
            else children[parent[i]].add(i);
        }
        if (root == -1) throw new IllegalArgumentException("No root found (parent must contain -1).");

        long[] down = new long[n]; // use long to avoid intermediate overflow with large vals
        long[] up = new long[n];
        computeDown(root, children, val, down);
        computeUp(root, children, val, up);
        int[] ans = new int[n];
        for (int i = 0; i < n; ++i) {
            long mx = Math.max(down[i], up[i]);
            if (mx > Integer.MAX_VALUE) ans[i] = Integer.MAX_VALUE;
            else if (mx < Integer.MIN_VALUE) ans[i] = Integer.MIN_VALUE;
            else ans[i] = (int) mx;
        }
        return ans;
    }

    /**
     * post-order: compute down[node] = max(0, val[node], val[node] + maxChildDown)
     */
    private long computeDown(int node, List<Integer>[] children, int[] val, long[] down) {
        long bestChild = 0;  // empty path allowed

        for (int c : children[node]) {
            bestChild = Math.max(bestChild, computeDown(c, children, val, down));
        }

        down[node] = Math.max(0L, Math.max(val[node], val[node] + bestChild));

        return down[node];
    }


    /**
     * pre-order: compute up[child] = max(0, val[child], up[parent] + val[child])
     */
    private void computeUp(int root, List<Integer>[] children, int[] val, long[] up) {
        Arrays.fill(up, 0L);
        up[root] = Math.max(0L, val[root]);
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            int node = stack.pop();
            for (int c : children[node]) {
                long viaParent = up[node] + (long) val[c];
                long onlyChild = val[c];
                long empty = 0L;
                up[c] = Math.max(empty, Math.max(onlyChild, viaParent));
                stack.push(c);
            }
        }
    }

    // --- optional main for quick manual tests ---
    static void main(String[] args) {
        Solutiosn s = new Solutiosn();

        int n = 5;
        int[] parent = {-1, 0, 0, 1, 1};
        int[] val = {1, 2, 3, 4, -10};

        int[] ans = s.maxDownUpPathSums(n, parent, val);
        System.out.println(Arrays.toString(ans)); // expected [7 vs earlier conversation? see explanation]
        // For clarity: expected answer based on earlier definitions:
        // down = [7,6,3,4,0], up = [1,3,4,7,0], answer = max(down,up) = [7,6,4,7,0]
        // So printed array should be: [7,6,4,7,0]
    }
}


