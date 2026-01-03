import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Blocking {

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5); // capacity 5

        Runnable producer = () -> {
            int value = 0;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    queue.put(value);
                    System.out.println("producing " + value);
                    value++;
                    Thread.sleep(200);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable consumer = () -> {
            int value ;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    value = queue.take();
                    System.out.println("consuming " + value);
                    Thread.sleep(200);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        };

        new Thread(producer).start();
        new Thread(consumer).start();

    }
}
