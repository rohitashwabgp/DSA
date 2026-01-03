import java.util.concurrent.atomic.AtomicReference;

class Node<T> {
    final T value;
    final AtomicReference<Node<T>> next;

    Node(T value) {
        this.value = value;
        this.next = new AtomicReference<>(null);
    }
}

class LockFreeQueue<T> {

    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;

    public LockFreeQueue() {
        Node<T> dummy = new Node<>(null); // dummy node
        head = new AtomicReference<>(dummy);
        tail = new AtomicReference<>(dummy);
    }

    public void enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        while (true) {
            Node<T> curTail = tail.get();
            Node<T> next = curTail.next.get();

            if (curTail == tail.get()) {
                if (next == null) {
                    if (curTail.next.compareAndSet(null, newNode)) {
                        tail.compareAndSet(curTail, newNode);
                        return;
                    }
                } else {
                    // tail lagging, move forward
                    tail.compareAndSet(curTail, next);
                }
            }
        }
    }

    public T dequeue() {
        while (true) {
            Node<T> curHead = head.get();
            Node<T> curTail = tail.get();
            Node<T> next = curHead.next.get();

            if (curHead == head.get()) {
                if (curHead == curTail) {
                    if (next == null) return null; // queue empty
                    tail.compareAndSet(curTail, next); // tail lagging
                } else {
                    T value = next.value;
                    if (head.compareAndSet(curHead, next)) {
                        return value;
                    }
                }
            }
        }
    }
}

public class CasQueueApp {
}
