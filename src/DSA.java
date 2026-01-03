import java.util.*;

class TreeNode {
    TreeNode left;
    TreeNode right;
    int val;

    TreeNode(int val) {
        this.val = val;
    }
}

//You are given n cities numbered 0 to n-1.
//
//You are also given a cost matrix cost[i][j] representing the cost to go directly from city i to city j.
//
//Rules:
//
//Start from city 0
//
//Visit each city exactly once
//
//Return back to city 0
//
//Return the minimum total cost.

class TSP {
    static int N;
    static int[][] cost;
    static int[][] dp;
    static int ALL_VISITED;

    static int tsp(int mask, int u) {
        if (mask == ALL_VISITED) {
            return cost[u][0]; // return to start
        }

        if (dp[mask][u] != -1) {
            return dp[mask][u];
        }

        int ans = Integer.MAX_VALUE;

        for (int v = 0; v < N; v++) {
            if ((mask & (1 << v)) == 0) { // not visited
                int newAns = cost[u][v] +
                        tsp(mask | (1 << v), v);
                ans = Math.min(ans, newAns);
            }
        }

        return dp[mask][u] = ans;
    }

    public static int findMinCost(int[][] matrix) {
        cost = matrix;
        N = matrix.length;
        ALL_VISITED = (1 << N) - 1;
        dp = new int[1 << N][N];

        for (int i = 0; i < (1 << N); i++)
            Arrays.fill(dp[i], -1);

        return tsp(1, 0); // start from city 0
    }
}



class SignalTree {

    static class Edge {
        int to, cost;

        Edge(int to, int cost) {
            this.to = to;
            this.cost = cost;
        }
    }

    private static int dfs(int node, int parent, List<Edge>[] tree) {
        List<Integer> costs = new ArrayList<>();

        for (Edge e : tree[node]) {
            if (e.to == parent) continue; // avoid going back to parent
            int subCost = dfs(e.to, node, tree) + e.cost; // go down
            costs.add(subCost);
        }

        if (costs.isEmpty()) return 0; // leaf node

        int sum = 0;
        int maxChild = 0;
        for (int c : costs) {
            sum += 2 * c;   // normally go down and come back
            maxChild = Math.max(maxChild, c); // remember last branch
        }

        // remove the last return from the largest subtree
        return sum - maxChild;
    }

    public static void main(String[] args) {
        int n = 5;
        List<Edge>[] tree = new ArrayList[n];
        for (int i = 0; i < n; i++) tree[i] = new ArrayList<>();

        // edges: node1, node2, cost
        int[][] edges = {{0, 1, 3}, {0, 2, 2}, {1, 3, 4}, {1, 4, 1}};

        for (int[] e : edges) {
            tree[e[0]].add(new Edge(e[1], e[2]));
            tree[e[1]].add(new Edge(e[0], e[2])); // undirected
        }

        int totalCost = dfs(0, -1, tree);
        System.out.println("Minimum total signal cost: " + totalCost);
    }
}

public class DSA {
    private static int longestSubArray(int[] nums) {
        Map<Integer, Integer> freqMap = new HashMap<>();
        freqMap.put(0, -1);
        int sum = 0;
        int longest = 0;
        for (int i = 0; i < nums.length; i++) {
            sum = sum + nums[i];
            if (freqMap.containsKey(sum)) {
                longest = Math.max(longest, i - freqMap.get(sum));
            } else {
                freqMap.put(sum, i);
            }
        }
        return longest;
    }

    private static int maximumSumSubSeq(int[] nums) {
        if (nums.length == 0) return 0;
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }
        return dp[dp.length - 1];
    }

    private static boolean detectNegativeCycle(int vertices, int[][] edges) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[0] = 0;
        for (int i = 0; i < vertices; i++) {
            for (int[] edge : edges) {
                int u = edge[0];
                int v = edge[1];
                int w = edge[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    // If this is the Vth relaxation, then there is
                    // a negative cycle
                    if (i == vertices - 1) return true;
                    dist[v] = dist[u] + w;
                }
            }
        }
        return false;
    }

    private static boolean detectCycle(int vertices, int[][] edges) {
        List<Integer>[] adjacency = new List[vertices];
        int[] indegree = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            adjacency[i] = new ArrayList<>();
        }
        // build graph
        for (int[] e : edges) {
            int u = e[0], v = e[1];
            adjacency[u].add(v);
            indegree[v]++;
        }
        Queue<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < vertices; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }

        }

        int removed = 0;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            removed++;
            for (int item : adjacency[current]) {
                indegree[item]--;
                if (indegree[item] == 0) queue.offer(item);
            }

        }
        return removed != vertices;
    }

    private static int platforms(int[] arr, int[] dep) {
        Arrays.sort(arr);
        Arrays.sort(dep);

        int i = 0, j = 0;
        int platforms = 0, maxPlatforms = 0;

        while (i < arr.length && j < dep.length) {
            if (arr[i] <= dep[j]) {
                platforms++;
                i++;
            } else {
                platforms--;
                j++;
            }
            maxPlatforms = Math.max(maxPlatforms, platforms);
        }
        return maxPlatforms;
    }

    class Solution {
        public boolean wordBreak(String s, List<String> wordDict) {
            Boolean[][] memo = new Boolean[s.length() + 1][s.length() + 1];
            return dfs(s, new HashSet<>(wordDict), 0, 1, memo);
        }

        boolean dfs(String s, Set<String> dict, int left, int right, Boolean[][] memo) {
            if (left == s.length())
                return true;
            if (right > s.length())
                return false;

            if (memo[left][right] != null)
                return memo[left][right];

            // Option 1: commit word if found
            if (dict.contains(s.substring(left, right))) {
                if (dfs(s, dict, right, right + 1, memo)) {
                    return memo[left][right] = true;
                }
            }

            // Option 2: extend right
            boolean res = dfs(s, dict, left, right + 1, memo);
            return memo[left][right] = res;
        }
    }

    // retry
    private int[] bitManipulation(int[] nums) {
        int xor = 0;
        for (int num : nums) {
            xor = xor ^ num;
        }
        //now xor have xor of 2 unique numbers;
        int diffBit = xor & (-xor); // rightmost set bit

        int a = 0, b = 0;
        for (int num : nums) {
            if ((num & diffBit) == 0) a ^= num;
            else b ^= num;
        }
        return new int[]{a, b};
    }




    private static int propagateDown(TreeNode node, int sum) {
        if (node == null) return sum;
        sum = sum + node.val;
        int sumLeft = 0;
        if (node.left != null) {
            sumLeft = propagateDown(node.left, sum);
        }
        if (node.right != null) {
            sum = propagateDown(node.right, sumLeft);
        }
        return sum;
    }

    private static int contigousSum(int[] nums, int k) {
        int max = Integer.MIN_VALUE;
        int sum = 0;
        int left = 0;
        for (int i = 0; i < nums.length; i++) {
            int curr = nums[i];
            sum = sum + curr;
            if ((i - left + 1) > k) {
                left++;
                sum = sum - nums[left];
            }
            max = Math.max(sum, max);
        }
        return max;
    }

    private static int[] maxMin(int[] nums, int k) {
        int left = 0;
        int[] res = new int[nums.length - k + 1];
        Deque<Integer> minDeque = new ArrayDeque<>();
        Deque<Integer> maxDeque = new ArrayDeque<>();
        for (int i = 0; i < nums.length; i++) {
            while (!maxDeque.isEmpty() && maxDeque.peekFirst() <= i - k) maxDeque.pollFirst();

            while (!minDeque.isEmpty() && minDeque.peekFirst() <= i - k) minDeque.pollFirst();

            while (!minDeque.isEmpty() && nums[minDeque.peekLast()] > nums[i]) {
                minDeque.pollLast();
            }
            minDeque.addLast(i);

            while (!maxDeque.isEmpty() && nums[maxDeque.peekLast()] < nums[i]) {
                maxDeque.pollLast();
            }
            maxDeque.addLast(i);

            if (i >= k - 1) {
                res[i - k + 1] = nums[maxDeque.peekFirst()] - nums[minDeque.peekFirst()];
            }
        }
        return res;
    }

//    You are given n cities numbered 0 to n-1.
//
//    You are also given a cost matrix cost[i][j] representing the cost to go directly from city i to city j.
//
//    Rules:
//
//    Start from city 0
//
//    Visit each city exactly once
//
//    Return back to city 0
//
//    Return the minimum total cost.

    private static int travelingSalesMan(int[][] cost) {
        int size = cost.length;
        int ALL_VISITED = (1 << size) - 1;
        int[][] dp = new int[1 << size][size];
        for (int i = 0; i < (1 << size); i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }
        dp[1][0] = 0;
       for (int mask = 1; mask <= ALL_VISITED; mask++) {
            for (int city = 0; city < size; city++) {
                if ((mask & (1 << city)) == 0) continue;
                if (dp[mask][city] == Integer.MAX_VALUE) continue;

                // try next city
                for (int next = 0; next < size; next++) {
                    if ((mask & (1 << next)) != 0) continue;

                    int newMask = mask | (1 << next);
                    dp[newMask][next] = Math.min(dp[newMask][next], dp[mask][city] + cost[city][next]);
                }
            }
        }

        // return to city 0
        int ans = Integer.MAX_VALUE;
        for (int city = 1; city < size; city++) {
            ans = Math.min(ans, dp[ALL_VISITED][city] + cost[city][0]);
        }

        return ans;

    }




    public static void main(String[] args) {
        int[] nums = new int[]{3, 2, 7, 10};
        System.out.println(maximumSumSubSeq(nums));
        int[] numsZero = new int[]{15, -2, 2, -8, 1, 7, 10, 23};
        System.out.println("Zero Sum- " + longestSubArray(numsZero));
        int[][] edges = new int[][]{{0, 1}, {1, 2}, {2, 0}, {2, 3}};
        int vertices = 4;
        System.out.println(detectCycle(vertices, edges));
    }


}
