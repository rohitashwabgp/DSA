import java.util.HashMap;
import java.util.Map;

public class Prequisite {
    public int minNumberOfSemesters(int n, int[][] relations, int k) {
        int[] pre = new int[n];
        for (int[] r : relations) {
            int a = r[0] - 1, b = r[1] - 1;
            pre[b] |= 1 << a;
        }
        Map<Integer, Integer> memo = new HashMap<>();
        return dfs(n, k, pre, 0, memo);
    }

    private int dfs(int n, int k, int[] pre, int mask, Map<Integer, Integer> memo) {
        if (mask == (1 << n) - 1)
            return 0;
        if (memo.containsKey(mask))
            return memo.get(mask);

        int available = 0;
        for (int i = 0; i < n; i++) {
            if (((mask >> i) & 1) == 0 && (pre[i] & mask) == pre[i]) {
                available |= 1 << i;
            }
        }

        int res = Integer.MAX_VALUE;
        int countAvailable = Integer.bitCount(available);

        if (countAvailable <= k) {
            res = 1 + dfs(n, k, pre, mask | available, memo);
        } else {
            // Generate combinations of size k directly
            int[] courses = new int[countAvailable];
            int idx = 0;
            for (int i = 0; i < n; i++) {
                if (((available >> i) & 1) == 1)
                    courses[idx++] = i;
            }
            res = 1 + helperComb(n, k, pre, mask, courses, 0, 0, k, memo, res);
        }

        memo.put(mask, res);
        return res;
    }

    private int helperComb(int n, int k, int[] pre, int mask, int[] courses, int start, int chosenMask, int left,
            Map<Integer, Integer> memo, int res) {
        if (left == 0) {
            return Math.min(res, dfs(n, k, pre, mask | chosenMask, memo));
        }
        for (int i = start; i <= courses.length - left; i++) {
            res = Math.min(res,
                    helperComb(n, k, pre, mask, courses, i + 1, chosenMask | (1 << courses[i]), left - 1, memo, res));
        }
        return res;

    }

}
