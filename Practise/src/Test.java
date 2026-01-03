import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Given an integer array nums, return all the triplets
// [nums[i], nums[j], nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] + nums[k] == 0.
//Input: nums = [-1,0,1,2,-1,-4]
//Output: [[-1,-1,2],[-1,0,1]]
//Explanation:
//nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0.
//nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0.
//nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0.
//The distinct triplets are [-1,0,1] and [-1,-1,2].
// sum[i] + sum[i+1]
public class Test {

    public static void restoreIpAddresses(String s, int index, List<String> path, List<String> result) {
        if (path.size() == 4) {
            // If all characters are used, it's a valid IP
            if (index == s.length()) {
                result.add(String.join(".", path));
            }
            return;
        }

        // Try segment length 1 to 3
        for (int len = 1; len <= 3; len++) {
            if (index + len > s.length()) break;
            String segment = s.substring(index, index + len);
            if (segment.length() > 1 && segment.startsWith("0")) continue;
            int value = Integer.parseInt(segment);
            if (value > 255) continue;
            path.add(segment);
            restoreIpAddresses(s, index + len, path, result);
            path.removeLast();
        }

    }

    public static void permute(int[] nums, List<Integer> current, List<List<Integer>> result, boolean[] used) {

        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            current.add(nums[i]);
            used[i] = true;
            permute(nums, current, result, used);
            current.removeLast();
            used[i] = false;
        }
    }

    public static List<List<String>> partition(String s) {
//        int numberofOdd = 0;
//        Map<Character, Integer> freqMap = new HashMap<>();
//
//        for (int i = 0; i < s.length(); i++) {
//            freqMap.put(s.charAt(i), freqMap.getOrDefault(s.charAt(i), 0) + 1);
//        }
//        for (int value : freqMap.values()) {
//            if (value % 2 != 0) {
//                numberofOdd++;
//            }
//        }
        List<List<String>> output = new ArrayList<>();
        //  if (numberofOdd > 1) return output;
        backtrack(s, output, 0, new ArrayList<>());
        return output;
    }

    private static void backtrack(String s, List<List<String>> output, int index, ArrayList<String> current) {
        if (index == s.length()) {
            if (!current.isEmpty()) {
                output.add(new ArrayList<>(current));
                return;
            }
        }

        for (int i = index; i < s.length(); i++) {
            if (isPalindrome(s, index, i)) {
                current.add(s.substring(index, i + 1));
                backtrack(s, output, i + 1, current);
                current.removeLast();
            }
        }
    }

    private static boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }

    public static int maxNonAdjacentSum(int[] nums) {
        int size = nums.length;
        int[] dp = new int[size];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < size; i++) {
            dp[i] = Math.max(nums[i] + dp[i - 2], dp[i - 1]);
        }
        return dp[size - 1];
    }

    class RequestCounter {
        List<Long> timeStamp = new ArrayList<>();
    }

    static ConcurrentMap<String, Deque<Long>> requestCounter = new ConcurrentHashMap<>();

    public static boolean rateLimiter(String userId, long timeStamp) {
        Deque<Long> timeStamps = requestCounter.computeIfAbsent(userId, k -> new ArrayDeque<>());
        synchronized (timeStamps) {
            while (!timeStamps.isEmpty() && timeStamps.peek() <= (System.currentTimeMillis() - 60 * 1000)) {
                timeStamps.pop();
            }
        }
        if (timeStamps.size() >= 5) return false;
        timeStamps.add(timeStamp);

        return true;
    }


    class TokenBucket {
        int capacity;
        int fillRate;
        int currentCounter;
        long lastUpdate;

        public TokenBucket(int capacity, int fillRate, int currentCounter) {
            this.capacity = capacity;
            this.fillRate = fillRate;
            this.currentCounter = 0;
            this.lastUpdate = System.currentTimeMillis();
        }
    }

    static ConcurrentMap<String, TokenBucket> requestCounter1 = new ConcurrentHashMap<>();

    private boolean allow(String userId) {
        TokenBucket bucket = requestCounter1.computeIfAbsent(userId, (k) -> new TokenBucket(10, 1, 0));
        synchronized (bucket) {
            fillBucket(bucket);
            if (bucket.currentCounter > 0) {
                bucket.currentCounter--;
                return true;
            }
            return false;
        }
    }

    private void fillBucket(TokenBucket bucket) {
        long timeNow = System.currentTimeMillis();
        long timeSpend = timeNow - bucket.lastUpdate;
        bucket.currentCounter = Math.min(bucket.capacity, bucket.currentCounter + (int) timeSpend / 1000);
        bucket.lastUpdate = timeNow;
    }


    private boolean allow2(String userId) {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        try {
            TokenBucket bucket = requestCounter1.computeIfAbsent(userId, (k) -> new TokenBucket(10, 1, 0));

            fillBucket2(bucket, lock);
            lock.readLock().lock();
            if (bucket.currentCounter > 0) {
                bucket.currentCounter--;
                return true;
            }
            lock.readLock().unlock();
            return false;
        } finally {
            lock.readLock().unlock();
            lock.writeLock().unlock();
        }

    }

    private void fillBucket2(TokenBucket bucket, ReadWriteLock lock) {
        lock.writeLock().lock();
        long timeNow = System.currentTimeMillis();
        long timeSpend = timeNow - bucket.lastUpdate;
        bucket.currentCounter = Math.min(bucket.capacity, bucket.currentCounter + (int) timeSpend / 1000);
        bucket.lastUpdate = timeNow;
        lock.writeLock().unlock();
    }

    private int chooseHotelRoom(int[] b, int k) {
        int low = b[0];
        int high = b[b.length - 1];
        int out = -1;
        for (int bIn : b) {
            low = Math.min(low, bIn);
            high = Math.max(high, bIn);
        }
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (canPick(b, k, mid)) {
                out = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }

        }
        return out;
    }

    private boolean canPick(int[] b, int k, int high) {
        int count = 0;
        for (int j : b) {
            if (j <= high) {
                count++;
            }
        }
        return count >= k;
    }

    //nums[i - 1] * nums[i] * nums[i + 1]
    public static int maxCoins(int[] nums) {
        int[][] dp = new int[nums.length + 1][nums.length + 1];
        int[] numsAdded = new int[nums.length + 2];
        int i = 1;
        for (int num : nums) {
            numsAdded[i] = num;
            i++;
        }
        numsAdded[0] = 1;
        numsAdded[nums.length] = 1;
        //last ballon
        for (int length = 2; length < numsAdded.length + 2; length++) {
            for (int left = 0; left + length < numsAdded.length + 2; left++) {
                int right = left + length;
                for (int k = left + 1; k < right; k++) {
                    dp[left][right] = Math.max(dp[left][right], dp[left][k] + dp[k][right] + (numsAdded[left] * numsAdded[k] * numsAdded[right]));
                }
            }
        }
        return dp[0][nums.length - 1];
    }

    public static int matrixChainOrder(int[] arr) {
        int n = arr.length;

        // dp[i][j] = minimum cost to multiply matrices Ai..Aj
        int[][] dp = new int[n][n];

        // length = number of matrices in chain
        for (int len = 2; len < n; len++) {
            for (int i = 1; i + len - 1 < n; i++) {
                int j = i + len - 1;
                dp[i][j] = Integer.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    int cost = dp[i][k] + dp[k + 1][j] + arr[i - 1] * arr[k] * arr[j];

                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }

        return dp[1][n - 1];
    }

//    public static int maxFrequency() {
//        int[] nums = {0, 1, 0, 1, 1, 1, 0};
//        int zeroCount = 0;
//        int oneCount = 0;
//        int total = 0;
//        for (int i = 0; i < nums.length; i++) {
//            if (nums[i] == 0) {
//                zeroCount++;
//
//            }
//            if (nums[i] == 1) {
//                oneCount++;
//            }
//            if (zeroCount == oneCount) {
//                Math.max(zeroCount, )
//            }
//        } return total;
//    }


    public static void main(String[] args) {
//        String input = "25525511135";
//        List<String> result = new ArrayList<>();
//        restoreIpAddresses(input, 0, new ArrayList<>(), result);
//        result.forEach(System.out::println);

//        int[] nums = {1, 2, 3};
//        List<List<Integer>> result1 = new ArrayList<>();
//        boolean[] used = new boolean[nums.length];
//        permute(nums, new ArrayList<>(), result1, used);
//        result1.forEach(data -> {
//            System.out.println(data.toString());
//        });
//        nums = new int[]{2, 7, 9, 3, 1};
//        System.out.println(maxNonAdjacentSum(nums));
//        nums = new int[]{3, 1, 5, 8};
//        System.out.println(maxCoins(nums));
        int[] arr = {10, 30, 5, 60};
      //  System.out.println(maxFrequency()); // 4500
    }

}
