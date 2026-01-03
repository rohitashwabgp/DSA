private static int diffMaxmin(int[] input, int k) {
    Deque<Integer> minDeque = new ArrayDeque<>();
    Deque<Integer> maxDeque = new ArrayDeque<>();
    int left = 0;
    int maxLength = 0;
    for (int right = 0; right < input.length; right++) {

        while (!minDeque.isEmpty() && input[minDeque.peekLast()] > input[right]) {
            minDeque.pollLast();
        }
        minDeque.addLast(right);

        while (!maxDeque.isEmpty() && input[maxDeque.peekLast()] < input[right]) {
            maxDeque.pollLast();
        }
        maxDeque.addLast(right);

        while (!maxDeque.isEmpty() && !minDeque.isEmpty() && input[maxDeque.peekFirst()] - input[minDeque.peekFirst()] > k) {
            if (minDeque.peekFirst() == left) {
                minDeque.pollFirst();
            }
            if (!maxDeque.isEmpty() && maxDeque.peekFirst() == left) {
                maxDeque.pollFirst();
            }
            left++;
        }
        maxLength = Math.max(maxLength, right - left + 1);
    }
    return maxLength;
}

private static int[] nextGreater(int[] nums) {
    int[] left = new int[nums.length];
    Stack<Integer> stack = new Stack<>();
    for (int i = 0; i < nums.length; i++) {
        while (!stack.isEmpty() && stack.peek() >= nums[i]) {
            stack.pop();
        }
        left[i] = stack.isEmpty() ? -1 : stack.pop();
        stack.push(i);
    }
    return left;
}

public static int subarraySum(int[] nums, int k) {
    int count = 0;
    int sum = 0;
    Map<Integer, Integer> map = new HashMap<>();
    map.put(0, 1); // for subarrays starting at index 0

    for (int num : nums) {
        sum = sum + num;
        if (map.containsKey(sum - k)) {
            count = count + map.get(sum - k);
        }
        map.put(sum, map.getOrDefault(sum, 0) + 1);
    }
    return count;
}

public static int longestZeroSum(int[] nums) {
    Map<Integer, Integer> map = new HashMap<>();
    int sum = 0, maxLen = 0;

    for (int i = 0; i < nums.length; i++) {
        sum += nums[i];

        if (sum == 0) {
            maxLen = i + 1;
        } else if (map.containsKey(sum)) {
            maxLen = Math.max(maxLen, i - map.get(sum));
        } else {
            map.put(sum, i);
        }
    }

    return maxLen;
}


private static int longestSubarray(int[] nums, int k) {
    Map<Integer, Integer> mapFreq = new HashMap<>();
    mapFreq.put(0, 1);
    int sum = 0;
    int numberOfSub = 0;
    for (int num : nums) {
        sum = sum + num;
        int reminder = ((sum % k) + k) % k;
        if (mapFreq.containsKey(reminder)) {
            numberOfSub = numberOfSub + mapFreq.get(reminder);
        }
        mapFreq.put(reminder, mapFreq.getOrDefault(reminder, 0) + 1);
    }
    return numberOfSub;
}


void main() {
    int[] arr = new int[]{10, 1, 2, 4, 7, 2};
    int[] nums = new int[]{4, 5, 0, -2, -3, 1};
    int k = 5;
    IO.println(longestSubarray(nums, k));
    // System.out.println(Arrays.toString(nextGreater(arr)));
}
