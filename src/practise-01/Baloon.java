import java.util.Arrays;

public class Baloon {

    public static int burst(int[] baloons) {

        int[] extended = new int[baloons.length + 2];
        extended[0] = 1;
        extended[baloons.length + 1] = 1;
        for (int i = 0; i < baloons.length; i++) {
            extended[i + 1] = baloons[i];
        }
        int[][] dp = new int[baloons.length + 2][baloons.length + 2];
        for (int i = 0; i < extended.length; i++) {
            Arrays.fill(dp[i], -1);
        }
        return calculate(extended, dp, 0, extended.length - 1);
    }

    public static int burstIteration(int[] baloons) {
        int[] extended = new int[baloons.length + 2];
        extended[0] = 1;
        extended[baloons.length + 1] = 1;
        for (int i = 0; i < baloons.length; i++) {
            extended[i + 1] = baloons[i];
        }
        int[][] dp = new int[baloons.length + 2][baloons.length + 2];
        for (int length = 2; length < extended.length; length++) {
            for (int left = 0; left < extended.length - length; left++) {
                int right = left + length;
                for (int ind = left + 1; ind < right; ind++) {
                    int cost = extended[left] * extended[ind] * extended[right];
                    cost = cost + dp[left][ind] + dp[ind][right];
                    dp[left][right] = Math.max(dp[left][right], cost);
                }

            }
        }
        return dp[0][extended.length - 1];
    }

    private static int calculate(int[] extended, int[][] dp, int left, int right) {
        if (left + 1 == right)
            return 0;
        if (dp[left][right] != -1)
            return dp[left][right];
        int maxi = 0;
        for (int i = left + 1; i < right; i++) {
            int cost = extended[left] * extended[i] * extended[right];
            cost = cost + calculate(extended, dp, left, i) + calculate(extended, dp, i, right);
            maxi = Math.max(cost, maxi);
        }
        dp[left][right] = maxi;
        return maxi;
    }
}
