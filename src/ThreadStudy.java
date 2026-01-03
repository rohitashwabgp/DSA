import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ThreadStudy {
    private boolean aTurn = true;
    private final List<String> buffer = new LinkedList<>();
    Lock lock = new ReentrantLock();
    Condition not_empty = lock.newCondition();
    Condition not_full = lock.newCondition();

    private void printA() throws InterruptedException {
        lock.lockInterruptibly();

        try {
            while (buffer.size() == 5) not_full.await();
            buffer.addLast(String.valueOf(Math.random()));
            not_empty.signalAll();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    private void printB() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (buffer.isEmpty()) not_empty.await();
            System.out.println(buffer.removeLast());
            not_full.signalAll();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ThreadStudy study = new ThreadStudy();

        Thread t1 = new Thread(() -> {
            try {
                study.printA();
            } catch (InterruptedException e) {
            }
        }, "Thread-A");

        Thread t2 = new Thread(() -> {
            try {
                study.printB();
            } catch (InterruptedException e) {
            }
        }, "Thread-B");

        t1.start();
        t2.start();
    }
}
