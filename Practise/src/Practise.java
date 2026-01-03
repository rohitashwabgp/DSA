import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Practise {


    public static int subarraysWithKDistinct(int[] nums, int k) {
        int left = 0;
        Map<Integer, Integer> freMap = new HashMap<>();
        int maxLength = 0;
        for (int right = 0; right < nums.length; right++) {
            int current = nums[right];
            freMap.put(current, freMap.getOrDefault(current, 0) + 1);
            while (freMap.size() > k) {
                int leftChar = nums[left];
                freMap.put(leftChar, freMap.get(leftChar) - 1);
                if (freMap.get(leftChar) == 0) {
                    freMap.remove(leftChar);
                }
                left++;

            }

            maxLength = maxLength + right - left + 1;


        }
        return maxLength;
    }

    public static long countSubarrays(int[] nums, int minK, int maxK) {
        int lastMin = -1;
        int lastMax = -1;
        int lastBad = -1;
        long count = 0;

        for (int i = 0; i < nums.length; i++) {
            int x = nums[i];

            if (x < minK || x > maxK) {
                lastBad = i;
            }

            if (x == minK) {
                lastMin = i;
            }

            if (x == maxK) {
                lastMax = i;
            }

            count += Math.max(0, Math.min(lastMin, lastMax) - lastBad);
        }

        return count;
    }

    public static int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];

        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        Deque<Integer> dq = new ArrayDeque<>();
        int shortest = Integer.MAX_VALUE;

        for (int i = 0; i <= n; i++) {

            // Try to shrink from front
            while (!dq.isEmpty() && prefix[i] - prefix[dq.peekFirst()] >= k) {
                shortest = Math.min(shortest, i - dq.pollFirst());
            }

            // Maintain increasing prefix sums
            while (!dq.isEmpty() && prefix[i] <= prefix[dq.peekLast()]) {
                dq.pollLast();
            }

            dq.addLast(i);
        }

        return shortest == Integer.MAX_VALUE ? -1 : shortest;
    }


    public static int longestSubarray(int[] nums, int limit) {

        Deque<Integer> maxDeque = new ArrayDeque<>();
        Deque<Integer> minDeque = new ArrayDeque<>();

        int left = 0;
        int ans = 0;

        for (int right = 0; right < nums.length; right++) {

            while (!maxDeque.isEmpty() && nums[maxDeque.peekLast()] < nums[right]) {
                maxDeque.pollLast();
            }
            maxDeque.addLast(right);

            while (!minDeque.isEmpty() && nums[minDeque.peekLast()] > nums[right]) {
                minDeque.pollLast();
            }
            minDeque.addLast(right);

            while (nums[maxDeque.peekFirst()] - nums[minDeque.peekFirst()] > limit) {
                if (maxDeque.peekFirst() == left) maxDeque.pollFirst();
                if (minDeque.peekFirst() == left) minDeque.pollFirst();
                left++;
            }

            ans = Math.max(ans, right - left + 1);
        }

        return ans;


    }

    public static void main(String[] args) {
        // int[] nums = {1, 3, 6, 7, 9, 10, 6, 7, 3, 1};
        //  int limit = 4;
        // System.out.println(longestSubarray(nums, limit));
        int[] nums1 = {1, 2, 1, 2, 3};
        int k = 2;
        //  System.out.println(subarraysWithKDistinct(nums1, k)- subarraysWithKDistinct(nums1, k-1));
        int[] nums = {1, -1, 5};
        int k4 = 5;
        // System.out.println(shortestSubarray(nums, k4));
        int[] numsNext = {1, 3, 5, 2, 7, 5};
        int minK = 1;
        int maxK = 5;
        System.out.println(countSubarrays(numsNext, minK, maxK));
    }

}
