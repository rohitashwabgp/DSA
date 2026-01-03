import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

public class SlidingWindow {

    public int longestSubstring(String s, int k) {
        int maxLength = 0;
        for (int distinct = 1; distinct <= 26; distinct++) {
            int left = 0;
            int right = 0;
            int[] window = new int[26];
            int currentDistinct = 0;
            int charsAtLeastK = 0;
            while (right < s.length()) {
                int curr = s.charAt(right) - 'a';

                if (currentDistinct <= distinct) {
                    if (window[curr] == 0) {
                        currentDistinct++;
                    }
                    window[curr]++;
                    if (window[curr] == k) {
                        charsAtLeastK++;
                    }
                    right++;
                } else {
                    int currleft = s.charAt(left) - 'a';

                    if (window[currleft] == k) {
                        charsAtLeastK--;
                    }
                    if (window[currleft] == 1) {
                        currentDistinct--;
                    }
                    window[currleft]--;
                    left++;
                }

                // Check if all distinct chars satisfy the "freq >= k" rule
                if (currentDistinct == distinct && currentDistinct == charsAtLeastK) {
                    maxLength = Math.max(maxLength, right - left);
                }
            }
        }
        return maxLength;
    }

    public String minWindow(String s, String t) {
        if (t == null || t.length() == 0)
            return "";
        if (s == null || s.length() == 0)
            return "";

        int[] need = new int[128];
        int[] window = new int[128];

        int distinct = 0;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (need[c] == 0)
                distinct++; // first time this char is required
            need[c]++;
        }

        int left = 0;
        int acquired = 0; // how many distinct chars meet the required count
        int bestStart = 0;
        int bestLength = Integer.MAX_VALUE;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            // add current char to window
            window[current]++;

            // if this char is required and we've just reached the required count, increase
            // acquired
            if (need[current] > 0 && window[current] == need[current]) {
                acquired++;
            }

            // try to shrink while window is valid (all required distinct chars satisfied)
            while (acquired == distinct && left <= right) {
                // update best answer
                int windowLen = right - left + 1;
                if (windowLen < bestLength) {
                    bestLength = windowLen;
                    bestStart = left;
                }

                // remove left char from window and move left forward
                char leftChar = s.charAt(left);
                if (need[leftChar] > 0 && window[leftChar] == need[leftChar]) {
                    // by removing it we will no longer satisfy that char
                    acquired--;
                }
                window[leftChar]--;
                left++;
            }
        }

        return (bestLength == Integer.MAX_VALUE) ? "" : s.substring(bestStart, bestStart + bestLength);
    }

    public int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int left = 0, maxCount = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            int idx = s.charAt(right) - 'A';
            count[idx]++;
            if (count[idx] > maxCount)
                maxCount = count[idx];
            while (right - left + 1 - maxCount > k) {
                count[s.charAt(left) - 'A']--;
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    public List<Integer> findAnagrams(String s, String p) {
        int[] need = new int[128];
        for (int i = 0; i < p.length(); i++) {
            need[p.charAt(i)]++;
        }
        int[] window = new int[128];
        List<Integer> result = new ArrayList<>();
        int left = 0;

        for (int right = 0; right < p.length(); right++) {
            window[p.charAt(right)]++;
            // Shrink window if it exceeds pLen
            if (right - left + 1 > p.length()) {
                window[s.charAt(left) - 'a']--;
                left++;
            }

            // If window matches p's frequency, record the start index
            if (Arrays.equals(window, need)) {
                result.add(left);
            }

        }
        return result;
    }

    class Node {
    public int val;
    public List<Node> neighbors;
    public Node() {
        val = 0;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val) {
        val = _val;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val, ArrayList<Node> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
     public Node cloneGraph(Node node) {
        Deque<Node> q = new ArrayDeque<>();
        if(node == null) return node;
        Map<Node, Node> nodeMap = new HashMap<>();
        nodeMap.put(node, new Node(node.val)); 
        q.add(node);
        while(!q.isEmpty()) {
            Node currNode = q.poll();
           for(Node node2: currNode.neighbors){
                 if(!nodeMap.containsKey(node2)) {
                    nodeMap.put(node2,  new Node(node2.val));
                    q.add(node2);
                 }
                 nodeMap.get(currNode).neighbors.add(nodeMap.get(node2));
           }
        }
        return nodeMap.get(node);
    }

    private void backtrack(int[] candidates, int target, List<List<Integer>> result, int index, List<Integer> path) {
        if (target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        if (index >= candidates.length || target < 0)
            return;

        path.add(candidates[index]);
        backtrack(candidates, target - candidates[index], result, index, path);

        path.remove(path.size() - 1);
        backtrack(candidates, target, result, index + 1, path);
    }

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, target, result, 0, new ArrayList<>());
        return result;
    }

    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> freqIndexMap = new HashMap<>();

        freqIndexMap.put(0, 1);
        int sum = 0;
        int count = 0;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            if (freqIndexMap.containsKey(sum - k)) {
                count++;
            }

            freqIndexMap.put(sum, freqIndexMap.getOrDefault(sum - k, 0) + 1);

        }
        return count;
    }

}
