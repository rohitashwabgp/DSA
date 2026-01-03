import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

interface BaseQueue<T> {
    void put(T t);

    T take();
}

public class CustomBlockingQueue<T> implements BaseQueue<T> {

    private final List<T> store;
    private final int capacity;
    Lock lock = new ReentrantLock();
    Condition not_full = lock.newCondition();
    Condition not_empty = lock.newCondition();

    public CustomBlockingQueue(int capacity) {
        this.capacity = capacity;
        store = new LinkedList<>();
    }

    @Override
    public void put(T t) {
        try {
            lock.lockInterruptibly();
            while (store.size() == capacity) {
                not_full.await();
            }
            store.add(t);
            not_empty.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException("Unexpected error...exiting ... " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }


    @Override
    public T take() {
        try {
            lock.lockInterruptibly();
            while (store.isEmpty()) {
                not_empty.await();
            }
            T item = store.removeFirst();
            not_full.signal();
            return item;
        } catch (InterruptedException ex) {
            throw new RuntimeException("Unexpected error...exiting... " + ex.getMessage());
        } finally {
            lock.unlock();
        }
    }
}

