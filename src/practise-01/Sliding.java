import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sliding {

    public int lengthOfLongestSubstring(String s) {
        int left = 0;
        int maxLength = 0;
        Map<Character, Integer> freqMap = new HashMap<>();
        for (int right = 0; right < s.length(); right++) {
            char curr = s.charAt(right);
            maxLength = Math.max(maxLength, right - left + 1);
            while (freqMap.containsKey(curr)) {
                char leftChar = s.charAt(left);
                freqMap.remove(leftChar);
                left++;
            }
            freqMap.put(curr, freqMap.getOrDefault(curr, 0) + 1);
        }
        return maxLength;
    }

    public int atMostK(String s, int atMost) {
        int left = 0;
        Map<Character, Integer> freqCount = new HashMap<>();
        int max = 0;
        for (int right = 0; right < s.length(); right++) {
            char curr = s.charAt(right);
            freqCount.put(curr, freqCount.getOrDefault(curr, 0) + 1);
            while (freqCount.size() > atMost) {
                char leftChar = s.charAt(left);
                freqCount.put(leftChar, freqCount.get(leftChar) - 1);
                if (freqCount.get(leftChar) == 0) {
                    freqCount.remove(leftChar);
                }
                left++;
            }
            max = Math.max(max, right - left + 1);

        }
        return max;
    }

    public int longestSubstring(String s, int k) {
        if (s == null || s.length() == 0)
            return 0;

        int maxLength = 0;
        int n = s.length();

        // Try all possible counts of distinct characters in the window
        for (int allowedDistinct = 1; allowedDistinct <= 26; allowedDistinct++) {

            int[] freq = new int[26];
            int windowStart = 0;
            int windowEnd = 0;

            int currentDistinct = 0; // distinct chars currently inside window
            int charsAtLeastK = 0; // how many chars appear >= k times

            while (windowEnd < n) {

                // Expand the window if still allowed to include more distinct chars
                if (currentDistinct <= allowedDistinct) {

                    int idx = s.charAt(windowEnd) - 'a';

                    if (freq[idx] == 0)
                        currentDistinct++;

                    freq[idx]++;

                    if (freq[idx] == k)
                        charsAtLeastK++;

                    windowEnd++;

                } else {
                    // Shrink until distinct chars drop back to allowedDistinct
                    int idx = s.charAt(windowStart) - 'a';

                    if (freq[idx] == k)
                        charsAtLeastK--;

                    freq[idx]--;

                    if (freq[idx] == 0)
                        currentDistinct--;

                    windowStart++;
                }

                // Check if all distinct chars satisfy the "freq >= k" rule
                if (currentDistinct == allowedDistinct && currentDistinct == charsAtLeastK) {
                    maxLength = Math.max(maxLength, windowEnd - windowStart);
                }
            }
        }

        return maxLength;
    }

    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> output = new ArrayList<>();
        if (s.length() < p.length())
            return output;

        int[] pFreq = new int[26];
        int[] windowFreq = new int[26];

        // Build frequency array for p
        for (char c : p.toCharArray()) {
            pFreq[c - 'a']++;
        }

        int left = 0;
        int pLen = p.length();

        for (int right = 0; right < s.length(); right++) {
            // Add current character to the window
            windowFreq[s.charAt(right) - 'a']++;

            // Shrink window if it exceeds pLen
            if (right - left + 1 > pLen) {
                windowFreq[s.charAt(left) - 'a']--;
                left++;
            }

            // If window matches p's frequency, record the start index
            if (Arrays.equals(windowFreq, pFreq)) {
                output.add(left);
            }
        }

        return output;
    }



    private int partiton(int[] nums, int left, int right) {
        int pivot = nums[right];
        int j = left;
        for (int i = left; i < right; i++) {
            if (nums[i] <= pivot) {
                swap(i, j, nums);
                j++;
            }
        }
        swap(right, j, nums);
        return j;
    }

    private void swap(int left, int right, int[] nums) {
        int temp = nums[left];
        nums[left] = nums[right];
        nums[right] = temp;
    }

    private int quick(int[] nums, int target, int left, int right) {
        if (left == right)
            return nums[left];
        int partiton = partiton(nums, left, right);
        if (target == partiton)
            return nums[partiton];
        else if (partiton > target) {
            return quick(nums, target, left, partiton - 1);
        } else {
            return quick(nums, target, partiton + 1, right);
        }
    }

    public int findKthLargest(int[] nums, int k) {
        return quick(nums, nums.length - k, 0, nums.length - 1);
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode slow = head;
        ListNode fast = head;
        int fastCount = n;
        while (fast != null && fastCount > 0) {
            fast = fast.next;
            fastCount--;
        }

        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }
        slow.next = slow.next.next;
        return head;
    }

    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target)
                return mid;

            // Left part is sorted
            if (nums[left] <= nums[mid]) {
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            // Right part is sorted
            else {
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return -1;
    }

    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];
        merged.add(current);

        for (int i = 1; i < intervals.length; i++) {
            int[] next = intervals[i];

            if (current[1] >= next[0]) { // overlap
                current[1] = Math.max(current[1], next[1]);
            } else {
                current = next;
                merged.add(current);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int target, int index,
            List<Integer> current, List<List<Integer>> result) {

        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (index >= candidates.length || target < 0) {
            return;
        }

        // OPTION 1: Include current element
        current.add(candidates[index]);
        backtrack(candidates, target - candidates[index], index, current, result);

        // BACKTRACK
        current.remove(current.size() - 1);

        // OPTION 2: Skip current element
        backtrack(candidates, target, index + 1, current, result);
    }

    public int subarraySum(int[] nums, int k) {
        int count = 0;
        int left = 0;
        int sum = 0;

        for (int i = 0; i < nums.length; i++) {
            int current = nums[i];
            sum = sum + current;
            if (sum == k)
                count++;
            while (sum > k && i > left) {
                int leftChar = nums[left];
                sum = sum - leftChar;
                left++;
            }
        }
        return count;
    }

    public int characterReplacement1(String s, int k) {
        if (s == null || s.length() == 0)
            return 0;

        int[] freq = new int[26];
        int left = 0;
        int maxCount = 0; // max frequency of a single char in current window (historical max works)
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            int r = s.charAt(right) - 'A';
            freq[r]++;
            // update most frequent character count seen so far in the window
            if (freq[r] > maxCount)
                maxCount = freq[r];

            // If more than k replacements required, shrink window
            while ((right - left + 1) - maxCount > k) {
                freq[s.charAt(left) - 'A']--;
                left++;
                // Note: we DO NOT update maxCount downwards here; keeping the historical max is
                // fine
            }

            // update best length
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    public int totalFruit(int[] fruits) {
        int left = 0;
        int max = 0;
        Map<Integer, Integer> freqCount = new HashMap<>();
        int currentMax = 0;
        for (int right = 0; right < fruits.length; right++) {
            int current = fruits[right];
            freqCount.put(current, freqCount.getOrDefault(current, 0) + 1);
            currentMax = currentMax + 1;
            while (freqCount.size() > 2 && freqCount.containsKey(left)) {
                int leftVal = fruits[left];
                freqCount.put(leftVal, freqCount.getOrDefault(leftVal, 0) - 1);
                if (freqCount.get(leftVal) == 0) {
                    freqCount.remove(leftVal);
                }
                currentMax--;
                left++;
            }
            max = Math.max(current, max);
        }
        return max;
    }

    public String minWindow(String s, String t) {
        if (s == null || t == null || s.length() < t.length())
            return "";
        Map<Character, Integer> freqCount = new HashMap<>();
        for (int i = 0; i < t.length(); i++) {
            freqCount.put(t.charAt(i), freqCount.getOrDefault(t.charAt(i), 0) + 1);
        }
        int left = 0;
        Map<Character, Integer> window = new HashMap<>();
        int achieved = 0;
        int minLength = Integer.MAX_VALUE;
        int bestLeft = 0;
        for (int right = 0; right < s.length(); right++) {
            char curr = s.charAt(right);
            window.put(curr, window.getOrDefault(curr, 0) + 1);

            if (freqCount.containsKey(curr) && window.get(curr) == freqCount.get(curr).intValue()) {
                achieved++;
            }

            while (left <= right && achieved == freqCount.size()) {
                if (right - left + 1 < minLength) {
                    minLength = right - left + 1;
                    bestLeft = left;
                }
                char leftCh = s.charAt(left);
                window.put(leftCh, window.getOrDefault(leftCh, 0) - 1);
                if (freqCount.containsKey(leftCh) && window.get(leftCh) < freqCount.get(leftCh)) {
                    achieved--;
                }
                left++;
            }
        }
        return minLength == Integer.MAX_VALUE ? "" : s.substring(bestLeft, bestLeft + minLength);

    }

    public static void main(String args[]) {

    }
}