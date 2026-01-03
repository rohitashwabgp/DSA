//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.atomic.AtomicReference;
//
//final class BucketState {
//
//    final private long lastUpdate;
//    final private int currentSize;
//
//    public long getLastUpdate() {
//        return lastUpdate;
//    }
//
//    public BucketState(int currentSize, long lastUpdate) {
//        this.currentSize = currentSize;
//        this.lastUpdate = lastUpdate;
//    }
//
//    public int getCurrentSize() {
//        return currentSize;
//    }
//
//}
//
//
//class LockFreeBucket {
//    final private int capacity;
//    final private AtomicReference<BucketState> state;
//
//    public LockFreeBucket(int capacity, long now) {
//        this.capacity = capacity;
//        this.state = new AtomicReference<>(new BucketState(0, now));
//    }
//
//    public int getCapacity() {
//        return capacity;
//    }
//
//    public AtomicReference<BucketState> getState() {
//        return state;
//    }
//}
//
//interface RateLimiter {
//    boolean allowRequest(String request);
//}
//
//public class LockFreeTokenBucket implements RateLimiter {
//    ConcurrentMap<String, LockFreeBucket> store;
//    private int capacity;
//    private int fillRate;
//
//    LockFreeTokenBucket(int fillRate, int capacity) {
//        this.store = new ConcurrentHashMap<>();
//        this.fillRate = fillRate;
//        this.capacity = capacity;
//    }
//
//    @Override
//    public boolean allowRequest(String clientId) {
//        long timeNow = System.currentTimeMillis();
//        LockFreeBucket bucket = store.computeIfAbsent(clientId, (k) -> new LockFreeBucket(capacity, timeNow));
//        while (true) {
//            BucketState oldState = bucket.getState().get();
//            BucketState newState = calculateBucketState(oldState, timeNow, capacity);
//            if (newState == null) {
//                return false;
//            }
//            if (bucket.getState().compareAndSet(oldState, newState)) {
//                return true;
//            }
//            //CAS retry
//        }
//    }
//
//    private BucketState calculateBucketState(BucketState oldState, long now, int capacity) {
//        long elapsedSec = (now - oldState.getLastUpdate()) / 1000;
//        int tokens = oldState.getCurrentSize();
//        if (elapsedSec > 0) {
//            tokens = (int) Math.min(capacity, tokens + elapsedSec * fillRate);
//        }
//        if (tokens == 0) {
//            return null; // reject
//        }
//        return new BucketState(tokens - 1, now);
//    }
//
//}
