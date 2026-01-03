import java.util.HashMap;
import java.util.Map;

class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}

public class First {

    public static ListNode reverseKGroup(ListNode head, int k) {
        // Step 1: Check if there are at least k nodes
        ListNode temp = head;
        for (int i = 0; i < k; i++) {
            if (temp == null) return head; // Less than k nodes, return as is
            temp = temp.next;
        }

        // Step 2: Reverse k nodes
        ListNode prev = null;
        ListNode current = head;
        ListNode next = null;
        for (int i = 0; i < k; i++) {
            next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }

        // Step 3: Recurse for remaining nodes
        if (next != null) {
            head.next = reverseKGroup(next, k);
        }

        // prev is new head of this reversed group
        return prev;
    }


}
