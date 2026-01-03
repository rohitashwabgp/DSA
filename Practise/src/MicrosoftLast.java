import java.util.HashMap;
import java.util.Stack;

public class MicrosoftLast {

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }


    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    public static String removeDuplicates(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && stack.peek() == c) {
                stack.pop(); // remove the duplicate
            } else {
                stack.push(c);
            }
        }
        // Build the resulting string
        StringBuilder sb = new StringBuilder();
        for (char c : stack) {
            sb.append(c);
        }

        return sb.toString();
    }


    public TreeNode buildTree(int[] inorder, int[] postOrder) {
        // Build map for quick index lookup
        HashMap<Integer, Integer> inorderIndexMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderIndexMap.put(inorder[i], i);
        }

        return helper(postOrder, 0, postOrder.length - 1, 0, inorder.length - 1, inorderIndexMap);
    }

    private TreeNode helper(int[] postOrder, int postStart, int postEnd, int inStart, int inEnd, HashMap<Integer, Integer> inorderIndexMap) {
        if (postStart > postEnd || inStart > inEnd) return null;

        // Root is last element in postorder segment
        int rootVal = postOrder[postEnd];
        TreeNode root = new TreeNode(rootVal);

        // Root index in inorder
        int inRootIndex = inorderIndexMap.get(rootVal);
        int leftSize = inRootIndex - inStart;

        // Build left subtree
        root.left = helper(postOrder, postStart, postStart + leftSize - 1, inStart, inRootIndex - 1, inorderIndexMap);

        // Build right subtree
        root.right = helper(postOrder, postStart + leftSize, postEnd - 1, inRootIndex + 1, inEnd, inorderIndexMap);

        return root;
    }

    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode nextNode = curr.next; // save next
            curr.next = prev;              // reverse pointer
            prev = curr;                   // move prev
            curr = nextNode;               // move curr
        }

        return prev; // new head
    }

    public boolean hasCycle(ListNode head) {

        ListNode fast = head;
        ListNode slow = head;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast.val == slow.val) {
                return true;
            }
        }
        return false;
    }


    class Solution {
        public ListNode detectCycle(ListNode head) {
            ListNode fast = head;
            ListNode slow = head;


            while (fast != null && fast.next != null) {
                fast = fast.next.next;
                slow = slow.next;
                if (fast == slow) {
                    break;
                }
            }
            if (fast == null || fast.next == null) {
                return null;
            }
            slow = head;

            while (slow != fast) {
                fast = fast.next;
                slow = slow.next;
            }
            return slow;
        }

        public void removeCycle(ListNode head) {
            ListNode fast = head;
            ListNode slow = head;


            while (fast != null && fast.next != null) {
                fast = fast.next.next;
                slow = slow.next;
                if (fast == slow) {
                    break;
                }
            }
            if (fast == null || fast.next == null) {
                return;
            }
            slow = head;

            while (slow != fast) {
                fast = fast.next;
                slow = slow.next;
            }

            ListNode ptr = slow;

            while (ptr.next != slow) {
                ptr = ptr.next;
            }

            // Step 4: Remove cycle
            ptr.next = null;
        }
    }

    //Given the head of a linked list, reverse the nodes of the list k at a time and return the modified list.
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode temp = head;
        for (int i = 0; i < k; i++) {
            if (temp == null) return head;
            temp = temp.next;
        }

        // Step 2: Reverse k nodes
        ListNode prev = null;
        ListNode curr = head;

        for (int i = 0; i < k; i++) {
            ListNode nextNode = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nextNode;
        }

        // Step 3: Recursive call for remaining list
        head.next = reverseKGroup(curr, k);

        // Step 4: Return new head
        return prev;
    }


    public static void main(String[] args) {

    }
}
