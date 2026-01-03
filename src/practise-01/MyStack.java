import java.util.LinkedList;
import java.util.Queue;

class MyStack {
    Queue<Integer> q1 = new LinkedList<>();
    Queue<Integer> q2 = new LinkedList<>();

    public void push(int x) {
        q1.offer(x);
    }

    public int pop() {
        if (empty()) return -1;

        // move n-1 elements to q2
        while (q1.size() > 1) {
            q2.offer(q1.poll());
        }

        int top = q1.poll();  // last element

        // swap
        Queue<Integer> temp = q1;
        q1 = q2;
        q2 = temp;

        return top;
    }

    public int top() {
        if (empty()) return -1;

        while (q1.size() > 1) {
            q2.offer(q1.poll());
        }

        int top = q1.poll();
        q2.offer(top);   // put it back

        Queue<Integer> temp = q1;
        q1 = q2;
        q2 = temp;

        return top;
    }

    public boolean empty() {
        return q1.isEmpty() && q2.isEmpty();
    }
}
