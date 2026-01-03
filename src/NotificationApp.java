//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//enum NOTIFICATION_TYPE {
//    EMAIL, SMS, PUSH
//}
//
///* ======================= DOMAIN ======================= */
//
//class Message {
//    private String body;
//
//    Message(String body) {
//        this.body = body;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//}
//
///* ======================= STRATEGY ======================= */
//
//interface Notification<T> {
//    NOTIFICATION_TYPE getType();
//    boolean send(T target, List<Message> messages);
//}
//
///* ======================= CONCRETE STRATEGIES ======================= */
//
//class EmailNotification implements Notification<String> {
//    public NOTIFICATION_TYPE getType() { return NOTIFICATION_TYPE.EMAIL; }
//
//    public boolean send(String email, List<Message> messages) {
//        messages.forEach(m ->
//                System.out.println("EMAIL -> " + email + " : " + m.getBody()));
//        return false; // simulate failure
//    }
//}
//
//class SMSNotification implements Notification<Long> {
//    public NOTIFICATION_TYPE getType() { return NOTIFICATION_TYPE.SMS; }
//
//    public boolean send(Long phone, List<Message> messages) {
//        messages.forEach(m ->
//                System.out.println("SMS -> " + phone + " : " + m.getBody()));
//        return true;
//    }
//}
//
//class PushNotification implements Notification<Long> {
//    public NOTIFICATION_TYPE getType() { return NOTIFICATION_TYPE.PUSH; }
//
//    public boolean send(Long pushId, List<Message> messages) {
//        messages.forEach(m ->
//                System.out.println("PUSH -> " + pushId + " : " + m.getBody()));
//        return true;
//    }
//}
//
///* ======================= DECORATOR ======================= */
//
//abstract class NotificationDecorator<T> implements Notification<T> {
//    protected final Notification<T> delegate;
//
//    NotificationDecorator(Notification<T> delegate) {
//        this.delegate = delegate;
//    }
//
//    public NOTIFICATION_TYPE getType() {
//        return delegate.getType();
//    }
//}
//
//class LoggingDecorator<T> extends NotificationDecorator<T> {
//
//    LoggingDecorator(Notification<T> delegate) {
//        super(delegate);
//    }
//
//    public boolean send(T target, List<Message> messages) {
//        System.out.println("[LOG] Before send");
//        boolean result = delegate.send(target, messages);
//        System.out.println("[LOG] After send");
//        return result;
//    }
//}
//
//class EncryptionDecorator<T> extends NotificationDecorator<T> {
//
//    EncryptionDecorator(Notification<T> delegate) {
//        super(delegate);
//    }
//
//    public boolean send(T target, List<Message> messages) {
//        messages.forEach(m ->
//                m.setBody(
//                        java.util.Base64.getEncoder()
//                                .encodeToString(m.getBody()
//                                        .getBytes(StandardCharsets.UTF_8))
//                ));
//        return delegate.send(target, messages);
//    }
//}
//
//class RetryDecorator<T> extends NotificationDecorator<T> {
//
//    private static final ScheduledExecutorService scheduler =
//            Executors.newScheduledThreadPool(1);
//
//    RetryDecorator(Notification<T> delegate) {
//        super(delegate);
//    }
//
//    public boolean send(T target, List<Message> messages) {
//        boolean success = delegate.send(target, messages);
//        if (!success) {
//            System.out.println("[RETRY] Scheduling retry...");
//            scheduler.schedule(
//                    () -> delegate.send(target, messages),
//                    5,
//                    TimeUnit.SECONDS
//            );
//        }
//        return true;
//    }
//}
//
///* ======================= COMPOSITE ======================= */
//
//class CompositeNotification<T> implements Notification<T> {
//
//    private final CopyOnWriteArrayList<Notification<T>> notifications =
//            new CopyOnWriteArrayList<>();
//
//    void add(Notification<T> notification) {
//        notifications.add(notification);
//    }
//
//    void remove(Notification<T> notification) {
//        notifications.remove(notification);
//    }
//
//    public NOTIFICATION_TYPE getType() {
//        return null; // not applicable
//    }
//
//    public boolean send(T target, List<Message> messages) {
//        notifications.forEach(n -> n.send(target, messages));
//        return true;
//    }
//}
//
///* ======================= APP ======================= */
//
//public class NotificationApp {
//
//    public static void main(String[] args) {
//
//        Message msg = new Message("Order Created");
//
//        /* ===== Strategy ===== */
//        Notification<String> email =
//                new EmailNotification();
//
//        /* ===== Runtime Decorator attach ===== */
//        Notification<String> emailWithFeatures =
//                new RetryDecorator<>(
//                        new EncryptionDecorator<>(
//                                new LoggingDecorator<>(email)
//                        )
//                );
//
//        /* ===== Composite ===== */
//        CompositeNotification<Object> composite =
//                new CompositeNotification<>();
//
//        composite.add((Notification<Object>) emailWithFeatures);
//        composite.add((Notification<Object>) new SMSNotification());
//        composite.add((Notification<Object>) new PushNotification());
//
//        /* ===== Send ===== */
//        composite.send(
//                "user@email.com",
//                List.of(msg)
//        );
//    }
//}
