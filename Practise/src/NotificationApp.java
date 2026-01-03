import java.util.Map;
import java.util.concurrent.*;

// Message class
class Message {
    final String type;
    final String content;
    final String userId;

    Message(String type, String content, String userId) {
        this.type = type;
        this.content = content;
        this.userId = userId;
    }
}

// NotificationChannel Interface
interface NotificationChannel {
    void publish(String message, String userId);
    String getType();
}

// Concrete channels
class EmailNotification implements NotificationChannel {
    private final BlockingQueue<Message> queue;
    EmailNotification(BlockingQueue<Message> queue) { this.queue = queue; }

    @Override
    public void publish(String message, String userId) {
        boolean offered = queue.offer(new Message(getType(), message, userId));
        if (!offered) System.out.println("Queue full: Dropped Email for " + userId);
    }

    @Override
    public String getType() { return "EMAIL"; }
}

class SMSNotification implements NotificationChannel {
    private final BlockingQueue<Message> queue;
    SMSNotification(BlockingQueue<Message> queue) { this.queue = queue; }

    @Override
    public void publish(String message, String userId) {
        boolean offered = queue.offer(new Message(getType(), message, userId));
        if (!offered) System.out.println("Queue full: Dropped SMS for " + userId);
    }

    @Override
    public String getType() { return "SMS"; }
}

class PushNotification implements NotificationChannel {
    private final BlockingQueue<Message> queue;
    PushNotification(BlockingQueue<Message> queue) { this.queue = queue; }

    @Override
    public void publish(String message, String userId) {
        boolean offered = queue.offer(new Message(getType(), message, userId));
        if (!offered) System.out.println("Queue full: Dropped Push for " + userId);
    }

    @Override
    public String getType() { return "PUSH"; }
}

// Consumer
class EventConsumer implements Runnable {
    private final BlockingQueue<Message> queue;
    EventConsumer(BlockingQueue<Message> queue) { this.queue = queue; }

    @Override
    public void run() {
        try {
            while (true) {
                Message msg = queue.take(); // blocking
                // simulate send
                System.out.printf("Sent [%s] to %s: %s%n", msg.type, msg.userId, msg.content);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Factory to manage channels & queues
class NotificationService {
    private final Map<String, NotificationChannel> channels;
    private final Map<String, BlockingQueue<Message>> queues;
    private final ExecutorService consumerPool;

    NotificationService() {
        queues = Map.of(
                "EMAIL", new ArrayBlockingQueue<>(100),
                "SMS", new ArrayBlockingQueue<>(100),
                "PUSH", new ArrayBlockingQueue<>(100)
        );

        channels = Map.of(
                "EMAIL", new EmailNotification(queues.get("EMAIL")),
                "SMS", new SMSNotification(queues.get("SMS")),
                "PUSH", new PushNotification(queues.get("PUSH"))
        );

        consumerPool = Executors.newFixedThreadPool(queues.size());
        queues.forEach((type, queue) -> consumerPool.submit(new EventConsumer(queue)));
    }

    public NotificationChannel getChannel(String type) {
        return channels.get(type);
    }

    public void shutdown() {
        consumerPool.shutdownNow();
    }
}

// Demo
public class NotificationApp {
    public static void main(String[] args) throws InterruptedException {
        NotificationService service = new NotificationService();

        // publish some messages
        service.getChannel("EMAIL").publish("Hello Email!", "user1");
        service.getChannel("SMS").publish("Hello SMS!", "user2");
        service.getChannel("PUSH").publish("Hello Push!", "user3");

        Thread.sleep(2000); // allow messages to be processed
        service.shutdown();
    }
}
