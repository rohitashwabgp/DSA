import java.util.*;

public class Lily {

    public static int lilysHomework(int[] arr) {
        // Count swaps for ascending order
        int ascSwaps = countSwaps(arr, true);
        // Count swaps for descending order
        int descSwaps = countSwaps(arr, false);
        // Return the minimum
        return Math.min(ascSwaps, descSwaps);
    }

    private static int countSwaps(int[] arr, boolean ascending) {
        int n = arr.length;

        // Pair each value with its original index
        int[][] arrPos = new int[n][2];
        for (int i = 0; i < n; i++) {
            arrPos[i][0] = arr[i];
            arrPos[i][1] = i;
        }

        // Sort by value (ascending or descending)
        Arrays.sort(arrPos, (a, b) -> ascending ? 
            Integer.compare(a[0], b[0]) : Integer.compare(b[0], a[0]));

        // Track visited elements
        boolean[] visited = new boolean[n];
        int swaps = 0;

        // Cycle detection
        for (int i = 0; i < n; i++) {
            // If element already in correct position or visited, skip
            if (visited[i] || arrPos[i][1] == i)
                continue;

            int cycleSize = 0;
            int j = i;

            // Visit all elements in this cycle
            while (!visited[j]) {
                visited[j] = true;
                j = arrPos[j][1];
                cycleSize++;
            }

            if (cycleSize > 1)
                swaps += (cycleSize - 1);
        }

        return swaps;
    }

    // Example run
    public static void main(String[] args) {
        int[] arr = {7, 15, 12, 3};
        System.out.println(lilysHomework(arr)); // Output: 2
    }
}
