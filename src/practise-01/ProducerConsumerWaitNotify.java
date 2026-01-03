// ProducerConsumerWaitNotify.java
public class ProducerConsumerWaitNotify {

    // Bounded buffer implemented with an array, head/tail/count
    static class BoundedBuffer<T> {
        private final Object[] buffer;
        private int head = 0, tail = 0, count = 0;

        public BoundedBuffer(int capacity) {
            if (capacity <= 0) throw new IllegalArgumentException("capacity > 0");
            buffer = new Object[capacity];
        }

        // put blocks if buffer full
        public synchronized void put(T item) throws InterruptedException {
            while (count == buffer.length) {
                wait(); // wait for space
            }
            buffer[tail] = item;
            tail = (tail + 1) % buffer.length;
            count++;
            notifyAll(); // wake up consumers
        }

        // take blocks if buffer empty
        @SuppressWarnings("unchecked")
        public synchronized T take() throws InterruptedException {
            while (count == 0) {
                wait(); // wait for items
            }
            T item = (T) buffer[head];
            buffer[head] = null; // help GC
            head = (head + 1) % buffer.length;
            count--;
            notifyAll(); // wake up producers
            return item;
        }

        public synchronized int size() {
            return count;
        }
    }

    // Simple producer that produces numbers
    static class Producer implements Runnable {
        private final BoundedBuffer<Integer> buf;
        private final int id;
        private final int produceCount;

        Producer(BoundedBuffer<Integer> buf, int id, int produceCount) {
            this.buf = buf; this.id = id; this.produceCount = produceCount;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < produceCount; i++) {
                    int item = id * 1000 + i;
                    buf.put(item);
                    System.out.printf("Producer %d produced %d (buffer=%d)%n", id, item, buf.size());
                    // simulate work
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Simple consumer that consumes numbers
    static class Consumer implements Runnable {
        private final BoundedBuffer<Integer> buf;
        private final int id;
        private final int consumeCount;

        Consumer(BoundedBuffer<Integer> buf, int id, int consumeCount) {
            this.buf = buf; this.id = id; this.consumeCount = consumeCount;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < consumeCount; i++) {
                    Integer item = buf.take();
                    System.out.printf("Consumer %d consumed %d (buffer=%d)%n", id, item, buf.size());
                    // simulate work
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int capacity = 5;
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(capacity);

        int producers = 2, consumers = 3;
        int produceEach = 20;
        int consumeEach = (producers * produceEach) / consumers;

        Thread[] ps = new Thread[producers];
        Thread[] cs = new Thread[consumers];

        for (int i = 0; i < producers; i++) {
            ps[i] = new Thread(new Producer(buffer, i + 1, produceEach), "P-" + (i + 1));
            ps[i].start();
        }
        for (int i = 0; i < consumers; i++) {
            cs[i] = new Thread(new Consumer(buffer, i + 1, consumeEach), "C-" + (i + 1));
            cs[i].start();
        }

        // wait for producers to finish
        for (Thread p : ps) p.join();
        // consumers will finish once they've consumed enough (we set consumeEach)
        for (Thread c : cs) c.join();

        System.out.println("All done (wait/notify demo).");
    }
}
