import java.util.LinkedList;

public class Wait {

    private final LinkedList<Integer> store = new LinkedList<>();
    private final int capacity = 5;

    class Producer implements Runnable {

        @Override
        public void run() {
            Integer value = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (store) {
                        while (capacity == store.size()) {
                            store.wait();
                        }
                        System.out.println("producing - value " + value);
                        store.addLast(value);
                        value++;
                        store.notifyAll();
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            int val;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (store) {
                        while (store.isEmpty()) {
                            store.wait();
                        }
                        val = store.removeFirst();
                        System.out.println("consuming - value " + val);
                        store.notifyAll();
                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Wait demo = new Wait();

        Thread producerThread = new Thread(demo.new Producer(), "Producer");
        Thread consumerThread = new Thread(demo.new Consumer(), "Consumer");

        producerThread.start();
        consumerThread.start();

        // Let them run for a while
        Thread.sleep(5000);

        // Request stop
        producerThread.interrupt();
        consumerThread.interrupt();

        // Wake up any waiters so they can exit promptly
        synchronized (demo.store) {
            demo.store.notifyAll();
        }

        // Wait for threads to finish
        producerThread.join();
        consumerThread.join();

        System.out.println("Demo finished.");
    }

}
