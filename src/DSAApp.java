import java.util.HashMap;
import java.util.Map;

class Solution {
    public int longestSubDistinctOne(String word, int k) {
        int left = 0;
        Map<Integer, Integer> window = new HashMap<>();
        int maxLength = 0;
        int maxFreq = 0;
        for (int right = 0; right < word.length(); right++) {
            int curr = word.charAt(right) - 'A';
            window.put(curr, window.getOrDefault(curr, 0) + 1);
            maxFreq = Math.max(maxFreq, window.get(curr));
            while ((right - left + 1) - maxFreq > k) {
                int leftCh = word.charAt(left) - 'A';
                window.put(leftCh, window.get(leftCh) - 1);
                if (window.get(leftCh) == 0) {
                    window.remove(leftCh);
                }
                left++;
            }
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    public long countSubsequences(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();

        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        long result = 0;

        for (int x : freq.keySet()) {
            if (freq.containsKey(x + 2)) {
                int c0 = freq.get(x);
                int c1 = freq.getOrDefault(x + 1, 0);
                int c2 = freq.get(x + 2);

                long waysMin = (1L << c0) - 1; // at least one x
                long waysMax = (1L << c2) - 1; // at least one x+2
                long waysMid = (1L << c1);    // optional x+1

                result += waysMin * waysMid * waysMax;
            }
        }

        return result;
    }

    class TreeNode {
        TreeNode left;
        TreeNode right;
        int val;

        TreeNode(int val) {
            this.val = val;
        }
    }

    public int rob(TreeNode root) {
        int[] res = dfs(root);
        return Math.max(res[0], res[1]);
    }

    private int[] dfs(TreeNode node) {
        // [take, notTake, blocked]
        if (node == null) return new int[]{0, 0, 0};

        int[] left = dfs(node.left);
        int[] right = dfs(node.right);

        int take = node.val + left[2] + right[2];

        int notTake = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);

        int blocked = left[1] + right[1];

        return new int[]{take, notTake, blocked};
    }


}


public class DSAApp {

    public static void main(String[] args) {
        Solution solution = new Solution();
        String input = "AABABBA";
        int k = 1;
        System.out.println(solution.longestSubDistinctOne(input, 1));
    }

}
