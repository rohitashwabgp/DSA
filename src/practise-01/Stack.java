// Node structure
class Node {
    int data;
    Node next;

    Node(int x) {
        data = x;
        next = null;
    }
}

// Stack implementation using linked list
public class Stack {
    Node top;

    // To Store current size of stack
    int count;

    Stack() {
        // initially stack is empty
        top = null;
        count = 0;
    }

    // push operation
    void push(int x) {
        Node temp = new Node(x);
        temp.next = top;
        top = temp;

        count++;
    }

    // pop operation
    int pop() {
        if (top == null) {
            System.out.println("Stack Underflow");
            return -1;
        }
        Node temp = top;
        top = top.next;
        int val = temp.data;

        count--;
        return val;
    }

    // peek operation
    int peek() {
        if (top == null) {
            System.out.println("Stack is Empty");
            return -1;
        }
        return top.data;
    }

    // check if stack is empty
    boolean isEmpty() {
        return top == null;
    }

    // size of stack
    int size() {
        return count;
    }

    public static void main(String[] args) {
        Stack st = new Stack();

        // pushing elements
        st.push(1);
        st.push(2);
        st.push(3);
        st.push(4);

        // popping one element
        System.out.println("Popped: " + st.pop());

        // checking top element
        System.out.println("Top element: " + st.peek());

        // checking if stack is empty
        System.out.println("Is stack empty: " + (st.isEmpty() ? "Yes" : "No"));

        // checking current size
        System.out.println("Current size: " + st.size());
    }
}