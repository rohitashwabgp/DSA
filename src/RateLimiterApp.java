//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//
//class Request {
//    final String userId;
//    final String api;
//
//    Request(String userId, String api) {
//        this.userId = userId;
//        this.api = api;
//    }
//
//    String key() {
//        return userId + "#" + api;
//    }
//}
//
//interface RateLimiter {
//    boolean allow(Request request);
//}
//
//
//class TokenBucketLimiter implements RateLimiter {
//
//    static class Bucket {
//        final int capacity;
//        final int refillRatePerSec;
//        AtomicLong tokens;
//        AtomicLong lastRefillTime;
//
//        Bucket(int capacity, int refillRatePerSec) {
//            this.capacity = capacity;
//            this.refillRatePerSec = refillRatePerSec;
//            this.tokens = new AtomicLong(capacity);
//            this.lastRefillTime = new AtomicLong(System.nanoTime());
//        }
//    }
//
//    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
//
//    @Override
//    public boolean allow(Request request) {
//        Bucket bucket = buckets.computeIfAbsent(
//                request.key(),
//                k -> new Bucket(10, 2)
//        );
//
//        refill(bucket);
//
//        long current;
//        do {
//            current = bucket.tokens.get();
//            if (current <= 0) return false;
//        } while (!bucket.tokens.compareAndSet(current, current - 1));
//
//        return true;
//    }
//
//    private void refill(Bucket bucket) {
//        long now = System.nanoTime();
//        long last = bucket.lastRefillTime.get();
//
//        long elapsedSec = (now - last) / 1_000_000_000;
//        if (elapsedSec > 0) {
//            long refill = elapsedSec * bucket.refillRatePerSec;
//            long newTokens = Math.min(bucket.capacity, bucket.tokens.get() + refill);
//            bucket.tokens.set(newTokens);
//            bucket.lastRefillTime.set(now);
//        }
//    }
//}
//
//
//class FixedWindowLimiter implements RateLimiter {
//
//    static class Window {
//        long windowStart;
//        AtomicInteger count = new AtomicInteger(0);
//    }
//
//    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
//    private final int limit = 10;
//    private final long windowSizeMs = 1000;
//
//    @Override
//    public boolean allow(Request request) {
//        long now = System.currentTimeMillis();
//
//        Window window = windows.computeIfAbsent(request.key(), k -> {
//            Window w = new Window();
//            w.windowStart = now;
//            return w;
//        });
//
//        synchronized (window) {
//            if (now - window.windowStart >= windowSizeMs) {
//                window.windowStart = now;
//                window.count.set(0);
//            }
//            return window.count.incrementAndGet() <= limit;
//        }
//    }
//}
//
//
//class SlidingWindowLimiter implements RateLimiter {
//
//    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> requests = new ConcurrentHashMap<>();
//    private final int limit = 10;
//    private final long windowMs = 1000;
//
//    @Override
//    public boolean allow(Request request) {
//        long now = System.currentTimeMillis();
//        ConcurrentLinkedQueue<Long> queue =
//                requests.computeIfAbsent(request.key(), k -> new ConcurrentLinkedQueue<>());
//
//        while (!queue.isEmpty() && now - queue.peek() > windowMs) {
//            queue.poll();
//        }
//
//        if (queue.size() >= limit) return false;
//
//        queue.offer(now);
//        return true;
//    }
//}
//
//
//class MetricsDecorator implements RateLimiter {
//
//    private final RateLimiter limiter;
//    AtomicInteger allowed = new AtomicInteger();
//    AtomicInteger blocked = new AtomicInteger();
//
//    MetricsDecorator(RateLimiter limiter) {
//        this.limiter = limiter;
//    }
//
//    @Override
//    public boolean allow(Request request) {
//        boolean result = limiter.allow(request);
//        if (result) allowed.incrementAndGet();
//        else blocked.incrementAndGet();
//        return result;
//    }
//}
//
//enum LIMITER_TYPE {
//    TOKEN_BUCKET, FIXED_WINDOW, SLIDING_WINDOW
//}
//
//class RateLimiterFactory {
//    static RateLimiter get(LIMITER_TYPE type) {
//        return switch (type) {
//            case TOKEN_BUCKET -> new MetricsDecorator(new TokenBucketLimiter());
//            case FIXED_WINDOW -> new MetricsDecorator(new FixedWindowLimiter());
//            case SLIDING_WINDOW -> new MetricsDecorator(new SlidingWindowLimiter());
//        };
//    }
//}
//
//public class RateLimiterApp {
//
//}
