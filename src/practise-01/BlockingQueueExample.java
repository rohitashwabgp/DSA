import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class MyBlockingQueue<E> {
    final ReentrantLock lock = new ReentrantLock();
    final Condition notEmpty = lock.newCondition();
    final Condition notFull = lock.newCondition();
    final int capacity = 6;
    final LinkedList<E> list = new LinkedList<>();

    void put(E item) throws InterruptedException {
        lock.lock();
        try {
            while (list.size() == capacity) {
                notFull.await();
            }
            list.add(item);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    E take() throws InterruptedException {
        lock.lock();
        try {
            while (list.isEmpty()) {
                notEmpty.await();
            }
            E item = list.removeFirst();
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
