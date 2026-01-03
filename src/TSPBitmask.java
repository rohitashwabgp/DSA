import java.util.Arrays;

public class TSPBitmask {

    static int n;
    static int[][] dist;
    static int[][] dp;
    static final int INF = 1_000_000_000;

    public static void main(String[] args) {

        dist = new int[][]{
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
        };

        n = dist.length;
        dp = new int[n][1 << n];

        for (int[] row : dp)
            Arrays.fill(row, -1);

        int result = solve(0, 1); // start at city 0, mask = 0001
        System.out.println("Minimum cost = " + result);
    }

    private static int solve(int currentCity, int mask) {
        if (currentCity == (1 << n) - 1) {
            return dist[currentCity][0];
        }
        int ans = INF;
        if (dp[currentCity][mask] != -1)
            return dp[currentCity][mask];
        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) == 0) {
                int nextMast = mask | (i<<i);
                int cost = dist[currentCity][mask] + solve(i, nextMast);
                ans = Math.min(ans, cost);
            }
        }
        dp[currentCity][mask] = ans;
        return ans;
    }
}


