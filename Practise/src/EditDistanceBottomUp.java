public static int minEditDistance(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];

    // base cases
    for (int i = 0; i <= m; i++) dp[i][0] = i; // delete all from s1
    for (int j = 0; j <= n; j++) dp[0][j] = j; // insert all into s1

    // fill DP table
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1]; // no cost
            } else {
                int insert = dp[i][j - 1] + 1;
                int delete = dp[i - 1][j] + 1;
                int replace = dp[i - 1][j - 1] + 1;
                dp[i][j] = Math.min(insert, Math.min(delete, replace));
            }
        }
    }

    return dp[m][n];
}

void main() {
    String s1 = "intention";
    String s2 = "execution";
    IO.println(minEditDistance(s1, s2)); // prints 5
}
