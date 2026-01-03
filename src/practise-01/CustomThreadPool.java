import java.util.LinkedList;

public class CustomThreadPool {
    private static final LinkedList<Integer> store = new LinkedList<>();

    public static void main(String[] args) {
        Runnable producer = () -> {
            int val = 0;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (store) {
                        while (!store.isEmpty()) {
                            store.wait();
                        }
                        store.addLast(val);
                        System.out.println("producer value " + val);
                        val++;
                        store.notifyAll();
                    }
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

            }
        };

        Runnable consumer = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    synchronized (store) {
                        while (store.isEmpty()) {
                            store.wait();
                        }
                        System.out.println("conumer value " + store.removeLast());
                        store.notifyAll();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        new Thread(producer).start();
        new Thread(consumer).start();
    }

}