
import java.util.*;

public class Prequiste2 {

    private int[] prerequisites; // prerequisites bitmask for each course
    private int[] dp; // dp[mask] = min semesters to finish courses in mask
    private int n, k;

    public int minNumberOfSemesters(int n, int[][] relations, int k) {
        this.n = n;
        this.k = k;

        // Initialize prerequisites array
        prerequisites = new int[n];
        for (int[] r : relations) {
            int pre = r[0] - 1;
            int course = r[1] - 1;
            prerequisites[course] |= (1 << pre);
        }

        // Initialize dp array
        dp = new int[1 << n];
        Arrays.fill(dp, -1);

        // Initially, all courses are not taken, so mask = 111...1
        int allCoursesMask = (1 << n) - 1;

        return findMinSemesters(allCoursesMask);
    }

    private int findMinSemesters(int currentMask) {
        if (currentMask == 0)
            return 0; // base case: all courses done
        if (dp[currentMask] != -1)
            return dp[currentMask];

        List<Integer> bucket = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            // If course i not taken and all prerequisites are completed
            if (((1 << i) & currentMask) != 0 && (prerequisites[i] & currentMask) == 0) {
                bucket.add(i);
            }
        }

        int minSem = Integer.MAX_VALUE;

        if (bucket.size() <= k) {
            // Take all available courses this semester
            int nextMask = currentMask;
            for (int course : bucket) {
                nextMask &= ~(1 << course);
            }
            minSem = 1 + findMinSemesters(nextMask);
        } else {
            // Generate all combinations of size k
            List<Integer> combinations = new ArrayList<>();
            getAllCombinations(combinations, bucket, k, currentMask, 0);
            for (int comb : combinations) {
                minSem = Math.min(minSem, 1 + findMinSemesters(comb));
            }
        }

        dp[currentMask] = minSem;
        return minSem;
    }

    private void getAllCombinations(List<Integer> ans, List<Integer> bucket, int k,
            int currentMask, int start) {
        if (k == 0) {
            ans.add(currentMask);
            return;
        }

        for (int i = start; i < bucket.size(); i++) {
            int nextMask = currentMask & ~(1 << bucket.get(i)); // mark course as taken
            getAllCombinations(ans, bucket, k - 1, nextMask, i + 1);
        }
    }

    // Test
    public static void main(String[] args) {
        Prequiste2 sol = new Prequiste2();
        int n = 13;
        int[][] relations = {
                { 12, 8 }, { 2, 4 }, { 3, 7 }, { 6, 8 }, { 11, 8 }, { 9, 4 }, { 9, 7 }, { 12, 4 }, { 11, 4 }, { 6, 4 },
                { 1, 4 },
                { 10, 7 }, { 10, 4 }, { 1, 7 }, { 1, 8 }, { 2, 7 }, { 8, 4 }, { 10, 8 }, { 12, 7 }, { 5, 4 }, { 3, 4 },
                { 11, 7 },
                { 7, 4 }, { 13, 4 }, { 9, 8 }, { 13, 8 }
        };
        int k = 9;

        System.out.println(sol.minNumberOfSemesters(n, relations, k)); // Expected: 3
    }
}
