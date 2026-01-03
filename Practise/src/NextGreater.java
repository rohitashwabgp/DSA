import java.util.*;

public class NextGreater {

    private int[] nextGreater(int[] given) {
        int[] result = new int[given.length];
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < given.length; i++) {
            while (!stack.isEmpty() && given[stack.peek()] < given[i]) {
                result[i] = given[stack.pop()];
            }
            stack.push(i);
        }
        return result;
    }

    private static String substring(String searchIn, String toFind) {

        Map<Character, Integer> freqMap = new HashMap<>();
        int required = 0;
        for (char ch : toFind.toCharArray()) {
            freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
            required++;
        }
        int[] freqMapOrig = new int[128];
        int minLength = 0;
        int left = 0;
        int bestStart = left;
        int formed = 0;
        for (int right = 0; right < searchIn.length(); right++) {
            char current = searchIn.charAt(right);
            freqMapOrig[current]++;
            if (freqMap.containsKey(current) && freqMapOrig[current] == freqMap.get(current).intValue()) {
                formed++;
            }
            while (left <= right && formed == required) {
                char leftChar = searchIn.charAt(left);
                if (minLength == 0 || right - left + 1 < minLength) {
                    minLength = right - left + 1;
                    bestStart = left;
                }
                freqMapOrig[leftChar]--;
                if (freqMap.containsKey(leftChar) && freqMapOrig[leftChar] < freqMap.get(current)) {
                    formed--;
                }
                left++;
            }

        }
        return searchIn.substring(bestStart, bestStart + minLength);
    }

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode next;   // this is what you have to populate

        TreeNode(int val) {
            this.val = val;
            this.left = null;
            this.right = null;
            this.next = null;
        }
    }

    private TreeNode bfs(TreeNode node) {
        if (node == null) return null;
        Queue<TreeNode> treeNodes = new LinkedList<>();
        treeNodes.offer(node);
        while (!treeNodes.isEmpty()) {
            int size = treeNodes.size();
            TreeNode prev = null;
            for (int i = 0; i < size; i++) {
                TreeNode current = treeNodes.poll();
                if (prev != null) {
                    prev.next = current;
                }
                prev = current;
                if (current.left != null) treeNodes.offer(current.left);
                if (current.right != null) treeNodes.offer(current.right);
            }
            prev.next = null;
        }
        return node;
    }

    private TreeNode traverseP(TreeNode node) {
        TreeNode current = node;
        while (current != null) {
            TreeNode nextHead = null;
            TreeNode nextTail = null;
            while (current != null) {
                if (current.left != null) {
                    if (nextTail != null) {
                        nextTail.next = current.left;
                    } else {
                        nextHead = current.left;
                    }
                    nextTail = current.left; // did not understand why ?
                }

                if (current.right != null) {
                    if (nextTail != null) {
                        nextTail.next = current.right;
                    } else {
                        nextHead = current.right;
                    }
                    nextTail = current.right;// same dont know why

                }
                current = current.next;
            }

            current = nextHead;
        }
        return current;
    }


    public static void main(String[] args) {
        System.out.println("what a - " + substring("ADOBECODEBANC", "ABC"));
    }
}
