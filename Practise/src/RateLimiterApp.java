import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.*;

public class RateLimiterApp {


    interface Logger {
        boolean shouldLog(String userId, String message);

        void log(String userId, String message);
    }

//    class LoggerAspect {
//        long[] timestamp = new long[10];
//        int[] counter = new int[10];
//    }

    class UserLogger implements Logger {


        ConcurrentMap<String, Long> userMap = new ConcurrentHashMap<>();
        final int WINDOW = 10;
        final long NANO_TO_SECOND = 1000_000_000;

        @Override
        public boolean shouldLog(String userId, String message) {
            long timeNow = System.nanoTime();
            return userMap.compute(userId, (k, lastLog) -> {
                if (lastLog == null ||
                        (timeNow - lastLog) >= WINDOW * NANO_TO_SECOND) {
                    return timeNow;   // update only if allowed
                }
                return lastLog;   // reject, keep old timestamp
            }) == timeNow;
        }

        @Override
        public void log(String userId, String message) {
            if (shouldLog(userId, message)) {
                System.out.println("Logging Completed");
            }
        }
    }


    static class UserRequests {
        String user;
        String requestTime;

        public UserRequests(String user) {
            this.user = user;
        }
    }

    static class Token {
        int capacity;
        long lastTimeStamp;
        int fillRate;
        int currentlyAvailable;

        Token(int capacity, int fillRate) {
            this.capacity = capacity;
            this.fillRate = fillRate;
            this.currentlyAvailable = 0;
            this.lastTimeStamp = 0;
        }
    }

    static class TokenBucket {
        private final int capacity;
        private final int fillRate;
        ConcurrentMap<String, Token> store = new ConcurrentHashMap<>();

        public TokenBucket(int capacity, int fillRate) {
            this.capacity = capacity;
            this.fillRate = fillRate;
        }

        boolean allowRequest(long timestamp, String userId) {
            Token token = store.computeIfAbsent(userId, (v) -> new Token(this.capacity, this.fillRate));
            synchronized (token) {
                updateBucket(timestamp, token);
                if (token.currentlyAvailable > 0) {
                    token.currentlyAvailable--;
                    return true;
                }
                return false;
            }

        }

        private void updateBucket(long timestamp, Token token) {
            long timeSpend = timestamp - token.lastTimeStamp;
            long added = timeSpend * fillRate;
            token.currentlyAvailable = (int) Math.min(capacity, token.currentlyAvailable + added);
            token.lastTimeStamp = timestamp;
        }

    }

    record CacheObject(String key, String value, long ttlInSeconds, long requestTime) {
    }

    class TTLCache {

        ConcurrentMap<String, CacheObject> store = new ConcurrentHashMap<>();
        final PriorityQueue<CacheObject> cacheEvict = new PriorityQueue<>(Comparator.comparingLong((a) -> a.requestTime() + a.ttlInSeconds));


        void put(String key, String value, long ttlInSeconds) {
            // do not see any reason to keep old time or ttl :: compute can be changed to put
            synchronized (cacheEvict) {
                CacheObject object = store.put(key, new CacheObject(key, value, ttlInSeconds, System.nanoTime()));
                if (object != null) {
                    cacheEvict.remove(object);
                }
                cacheEvict.offer(store.get(key));
            }
        }

        String get(String key) {
            CacheObject object = store.getOrDefault(key, null);
            long unitConversion = 1000_000_000;
            if (object != null && object.ttlInSeconds >= (System.nanoTime() - object.requestTime()) / unitConversion) {
                return object.value();
            }
            return null;
        }

        void automaticEviction() {
            // can scale eviction
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
            scheduler.scheduleAtFixedRate(() -> {
                synchronized (cacheEvict) {
                    while (!cacheEvict.isEmpty() && cacheEvict.peek().ttlInSeconds * 1e9 <= System.nanoTime() - cacheEvict.peek().requestTime()) {

                        CacheObject toRemove = cacheEvict.poll();
                        store.remove(toRemove.key());
                    }
                }

            }, 0, 5, TimeUnit.MINUTES);

        }
    }


    class HitCounter {
        long[] timeStampBucket;
        int[] counterBucket;

        HitCounter() {
            this.counterBucket = new int[300];
            this.timeStampBucket = new long[300];
        }

        public void hit(long timeStamp) {
            int idx = (int) (timeStamp % 300);
            if (timeStampBucket[idx] != timeStamp) {
                timeStampBucket[idx] = timeStamp;
                counterBucket[idx] = 1;
            } else {
                counterBucket[idx]++;
            }
        }

        public int getHits(long timeNow) {
            int hits = 0;
            for (int i = 0; i < 300; i++) {
                if (timeNow - timeStampBucket[i] < 300) {
                    hits = hits + counterBucket[i];
                }
            }
            return hits;
        }

    }

    class HitCounter2 {
        TreeMap<Long, Integer> counterBucket;

        HitCounter2() {
            this.counterBucket = new TreeMap<>();
        }

        public void hit(long timeStamp) {
            counterBucket.put(timeStamp, counterBucket.getOrDefault(timeStamp, 0) + 1);
        }

        public int getHits(long timeNow) {
            long start = timeNow - 300;
            if (start < 0) {
                start = 0;
            }
            return counterBucket.tailMap(start).values().stream().mapToInt(i -> i).sum();
        }

    }

    class CommonConstant {

        final static int MAX_CAPACITY = 100;
    }

    static class UserRequest {
        String user;
        String requestTime;

        public UserRequest(String user) {
            this.user = user;
        }
    }

    static class Bucket {
        double currentFilled;
        long lastUpdateTime;

        Bucket() {
            this.currentFilled = 0;
            this.lastUpdateTime = System.nanoTime();
        }
    }

    @FunctionalInterface
    interface LeakyBucket {

        boolean isAllowed(UserRequest request);
    }

    static class LeakyBucketImpl implements LeakyBucket {
        ConcurrentHashMap<String, Bucket> store = new ConcurrentHashMap<>();

        @Override
        public boolean isAllowed(UserRequest request) {
            Bucket bucket = store.computeIfAbsent(request.user, k -> new Bucket());
            synchronized (bucket) {
                updateBucket(bucket);
                if (bucket.currentFilled < CommonConstant.MAX_CAPACITY) {
                    bucket.currentFilled++; // consume 1 request
                    return true;
                } else {
                    return false;
                }
            }
        }

        private void updateBucket(Bucket bucket) {
            long timeNow = System.nanoTime();
            double leakRatePerNs = 100.0 / 1_000_000_000.0;
            double leaked = ((timeNow - bucket.lastUpdateTime) * leakRatePerNs);
            bucket.currentFilled = Math.max(0, bucket.currentFilled - leaked);
            bucket.lastUpdateTime = timeNow;
        }

        public static void main(String[] args) throws InterruptedException {
            LeakyBucketImpl limiter = new LeakyBucketImpl();
            String user = "Alice";

//            System.out.println("Sending 100 requests instantly:");
//            for (int i = 1; i <= 500; i++) {
//                boolean allowed = limiter.isAllowed(new UserRequest(user));
//                System.out.printf("Request %d: %s\n", i, allowed ? "Allowed" : "Rejected");
//            }
//
//            System.out.println("\nSending 1 request per second for 10 seconds:");
//            for (int i = 1; i <= 10; i++) {
//                Thread.sleep(1000); // wait 1 second
//                boolean allowed = limiter.isAllowed(new UserRequest(user));
//                System.out.printf("Second %d request: %s\n", i, allowed ? "Allowed" : "Rejected");
//            }

            TokenBucket bucket = new TokenBucket(300, 1);
            int jump = 299;
            boolean allowed;
            for (int i = 1; i <= 500; i++) {

                if (i == 2) {
                    allowed = bucket.allowRequest(i + jump, "456");
                } else {
                    allowed = bucket.allowRequest(i, "456");
                }
                System.out.printf("Request %d: %s\n", i, allowed ? "Allowed" : "Rejected");
            }
        }
    }
}

// just for understanding ignore
//public boolean allowedFunctional(UserRequest requestOrig) {
//    LeakyBucket bucketFn = (UserRequest request) -> {
//        Bucket bucket;
//        if (!store.containsKey(request.user)) {
//            store.put(request.user, new Bucket());
//        }
//        bucket = store.get(request.user);
//        updateBucket(bucket);
//        if (bucket.currentFilled < CommonConstant.MAX_CAPACITY) {
//            bucket.currentFilled++; // consume 1 request
//            return true;
//        } else {
//            return false;
//        }
//    };
//    return bucketFn.isAllowed(requestOrig);
//}
