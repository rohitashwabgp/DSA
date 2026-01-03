import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class ProducerConsumer {

    private final LinkedList<Integer> buffer = new LinkedList<>();
    private final int capacity = 5;

    // Producer thread
    class Producer implements Runnable {
        @Override
        public void run() {
            int value = 0;

            try {
                while (true) {
                    synchronized (buffer) {
                        while (buffer.size() == capacity) {
                            buffer.wait();   // wait if buffer full
                        }

                        buffer.add(value);
                        System.out.println("Produced: " + value);
                        value++;

                        buffer.notifyAll(); // wake up consumers
                    }

                    Thread.sleep(300);  // simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Consumer thread
    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    int item;

                    synchronized (buffer) {
                        while (buffer.isEmpty()) {
                            buffer.wait();   // wait if buffer empty
                        }

                        item = buffer.removeFirst();
                        System.out.println("Consumed: " + item);

                        buffer.notifyAll(); // wake up producers
                    }

                    Thread.sleep(500); // simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        ProducerConsumer pc = new ProducerConsumer();

        Thread p = new Thread(pc.new Producer());
        Thread c = new Thread(pc.new Consumer());

        p.start();
        c.start();
    }
}




public class ProducerConsumerLock {

    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity = 5;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    class Producer implements Runnable {
        public void run() {
            int value = 0;
            try {
                while (true) {
                    lock.lock();
                    try {
                        while (buffer.size() == capacity)
                            notFull.await();

                        buffer.add(value);
                        System.out.println("Produced: " + value);
                        value++;

                        notEmpty.signal();   // wake consumers
                    } finally {
                        lock.unlock();
                    }
                    Thread.sleep(300);
                }
            } catch (Exception ignored) {}
        }
    }

    class Consumer implements Runnable {
        public void run() {
            try {
                while (true) {
                    lock.lock();
                    try {
                        while (buffer.isEmpty())
                            notEmpty.await();

                        int item = buffer.remove();
                        System.out.println("Consumed: " + item);

                        notFull.signal();  // wake producers
                    } finally {
                        lock.unlock();
                    }
                    Thread.sleep(500);
                }
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        ProducerConsumerLock pc = new ProducerConsumerLock();
        new Thread(pc.new Producer()).start();
        new Thread(pc.new Consumer()).start();
    }
}


public class ProducerConsumerBQ {

    public static void main(String[] args) {

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5); // capacity 5

        // Producer
        Runnable producer = () -> {
            int value = 0;
            try {
                while (true) {
                    queue.put(value);   // blocks if full
                    System.out.println("Produced: " + value);
                    value++;
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Consumer
        Runnable consumer = () -> {
            try {
                while (true) {
                    int item = queue.take(); // blocks if empty
                    System.out.println("Consumed: " + item);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        new Thread(producer).start();
        new Thread(consumer).start();
    }
}

