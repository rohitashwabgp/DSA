public class Garden {
    public static int minTaps(int n, int[] ranges) {
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


}
