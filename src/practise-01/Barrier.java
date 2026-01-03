import java.util.concurrent.CyclicBarrier;

public class Barrier {
    public static void main(String[] args) {
        int workers = 3;
        CyclicBarrier barrier = new CyclicBarrier(workers, 
            () -> System.out.println("All workers ready. Moving to next stage!"));

        for (int i = 1; i <= workers; i++) {
            int workerId = i;
            new Thread(() -> {
                try {
                    System.out.println("Worker " + workerId + " is working on stage 1");
                    Thread.sleep(1000); // simulate work
                    barrier.await();     // wait for others

                    System.out.println("Worker " + workerId + " is working on stage 2");
                    Thread.sleep(1000);
                    barrier.await();     // wait again for next stage

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
