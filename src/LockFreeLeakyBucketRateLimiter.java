//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.atomic.AtomicReference;
//
//final class BucketState {
//    final int current;
//    final long lastUpdatedMs;
//
//    BucketState(int current, long lastUpdatedMs) {
//        this.current = current;
//        this.lastUpdatedMs = lastUpdatedMs;
//    }
//}
//
//
//enum CLIENT_TYPE {
//    IP, USER, APIKEY
//}
//
//class ClientRequest {
//    CLIENT_TYPE type;
//    String clientId;
//
//    ClientRequest(CLIENT_TYPE type, String clientId) {
//        this.type = type;
//        this.clientId = clientId;
//    }
//}
//
//class LockFreeBucket {
//    final int capacity;
//    final AtomicReference<BucketState> state;
//
//    LockFreeBucket(int capacity, long now) {
//        this.capacity = capacity;
//        this.state = new AtomicReference<>(new BucketState(0, now));
//    }
//}
//
//interface RateLimiter {
//    boolean allowRequest(ClientRequest request);
//}
//
//public class LockFreeLeakyBucketRateLimiter implements RateLimiter {
//
//    private final ConcurrentMap<String, LockFreeBucket> buckets = new ConcurrentHashMap<>();
//    private final int leakRatePerSec;
//    private final int bucketCapacity;
//
//    public LockFreeLeakyBucketRateLimiter(int leakRatePerSec, int bucketCapacity) {
//        this.leakRatePerSec = leakRatePerSec;
//        this.bucketCapacity = bucketCapacity;
//    }
//
//    @Override
//    public boolean allowRequest(ClientRequest request) {
//        long now = System.currentTimeMillis();
//        String key = request.type + ":" + request.clientId;
//
//        LockFreeBucket bucket = buckets.computeIfAbsent(key, k -> new LockFreeBucket(bucketCapacity, now));
//
//        while (true) {
//            BucketState oldState = bucket.state.get();
//
//            BucketState newState = calculateNewState(oldState, now, bucket.capacity);
//            if (newState == null) {
//                return false;
//            }
//
//            if (bucket.state.compareAndSet(oldState, newState)) {
//                return true;
//            }
//            // CAS failed → retry
//        }
//    }
//
//    private BucketState calculateNewState(BucketState oldState, long now, int capacity) {
//        long elapsedSec = (now - oldState.lastUpdatedMs) / 1000;
//        int leaked = (int) (elapsedSec * leakRatePerSec);
//
//        int newCurrent = Math.max(0, oldState.current - leaked);
//
//        if (newCurrent >= capacity) {
//            return null; // reject request
//        }
//
//        return new BucketState(newCurrent + 1, now);
//    }
//}
