import java.util.Arrays;
import java.util.Comparator;

public class WaterGarden {

    private int[] getCoveredInterval(int[] intervals1, int[] intervals2) {
        if (intervals1[1] > intervals2[0] && intervals2[1] > intervals1[0]) {
            return new int[] { Math.min(intervals1[0], intervals2[0]), Math.max(intervals2[1], intervals1[1]) };
        }
        return null;
    }

    private int lengthOfOverlap(int[] intervals1, int[] intervals2) {
        return Math.max(0, Math.min(intervals1[1], intervals2[2]) - Math.max(intervals1[0], intervals2[0]));
    }

    public int minTaps(int n, int[] ranges) {
        int[][] intervals = new int[ranges.length - 1][];
        // int[] covered = new int[n];
        int[] area = { 0, n };
        int min = 483728492;
        for (int i = 0; i < ranges.length; i++) {
            intervals[i] = new int[] { i - ranges[i], i + ranges[i] };
        }

        for (int i = 0; i < intervals.length; i++) {
            var current = intervals[i];
            int count = 1;
            if (current[0] >= area[0] && current[1] >= area[1]) {
                min = Math.min(min, count);
            }
            for (int j = i + 1; j < ranges.length; j++) {
                int[] newInterval = getCoveredInterval(current, intervals[j]);
                if (newInterval != null) {
                    current = newInterval;
                    count++;
                    min = Math.min(min, count);
                } else {
                    if (lengthOfOverlap(current, area) < lengthOfOverlap(intervals[j], area)) {
                        current = intervals[j];
                    }
                }

            }
        }

        return 0;
    }

    // refer next

    public int minTaps4(int n, int[] ranges) {
        int[][] intervals = new int[ranges.length][2];

        for (int i = 0; i < ranges.length; i++) {
            intervals[i][0] = Math.max(0, i - ranges[i]);
            intervals[i][1] = Math.min(n, i + ranges[i]);
        }

        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        int taps = 0, end = 0, farthest = 0, i = 0;

        while (end < n) {
            while (i < intervals.length && intervals[i][0] <= end) {
                farthest = Math.max(farthest, intervals[i][1]);
                i++;
            }

            if (farthest == end)
                return -1;

            taps++;
            end = farthest;
        }

        return taps;
    }

    // refer 1
    public int minTaps1(int n, int[] ranges) {
        int[] right = new int[n + 1];

        // Fill the farthest reach from each starting point
        for (int i = 0; i <= n; i++) {
            int left = Math.max(0, i - ranges[i]);
            int r = Math.min(n, i + ranges[i]);
            right[left] = Math.max(right[left], r);
        }

        int taps = 0, curEnd = 0, farthest = 0;

        for (int i = 0; i <= n; i++) {
            if (i > farthest)
                return -1; // gap, cannot cover
            if (i > curEnd) {
                taps++;
                curEnd = farthest;
            }
            farthest = Math.max(farthest, right[i]);
        }

        return taps;
    }

    // DP
    public int minTaps3(int n, int[] ranges) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, n + 2); // use a large number
        dp[0] = 0;

        for (int i = 0; i <= n; i++) {
            int left = Math.max(0, i - ranges[i]);
            int right = Math.min(n, i + ranges[i]);
            // update dp for coverage extended by this tap
            for (int j = left + 1; j <= right; j++) {
                dp[j] = Math.min(dp[j], dp[left] + 1);
            }
        }

        return dp[n] <= n ? dp[n] : -1;
    }

}
