/*
Tree Node Values Transformation text
Problem: Given a tree with integer values at nodes.
Phase 1 (Down): For each node, calculate sum of its subtree values.
Phase 2 (Up): For each node, calculate sum of all values NOT in its subtree.
        Return: List of final values = down[node] + up[node].

Example:
n=4, edges=[[0,1],[0,2],[1,3]], values=[1,2,3,4]
Down: [10,6,3,4] (subtree sums)
Up: [0,4,7,6] (rest of tree)
Result: [10,10,10,10] (always total sum for all nodes)
*/

public class Problem1 {

    List<Integer> transformTreeValues(int n, List<int[]> edges, List<Integer> values) {
        List<Integer> result = new ArrayList<>();
        if (n <= 1) return result;
        int[] adj = new int[n];
        for (int[] edge : edges) {
            adj[edge[0]] = edge[1];
            adj[edge[1]] = edge[0];
        }
        int [] down = new int[n];
        calculateDownTree(n, edges, values, down);
    }

    public static void main(String[] args) {
        int n = 4;
        int[] edges = {{0, 1}, {0, 2}, {1, 3}};
        int[] values = {1, 2, 3, 4};
        System.out.println("problem 1 ---- ");
    }
}