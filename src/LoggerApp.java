import java.util.concurrent.atomic.AtomicReference;

interface Logger {
    boolean log(long timestamp, String message);
}

final class BucketState {
    public final long lastTimeStamp;
    public final long currentSize;

    public BucketState(long lastTimeStamp, long currentSize) {
        this.lastTimeStamp = lastTimeStamp;
        this.currentSize = currentSize;
    }
}

class ThreadSafeLogger implements Logger {
    private final int fillRate;
    private final int capacity;
    private final AtomicReference<BucketState> bucketState;

    public ThreadSafeLogger(int fillRate, int capacity) {
        this.capacity = capacity;
        this.fillRate = fillRate >= 0 ? fillRate : 1;
        this.bucketState = new AtomicReference<>(new BucketState(System.currentTimeMillis(), 10));
    }

    @Override
    public boolean log(long timestamp, String message) {
        long timeNow = System.currentTimeMillis();
        while (true) {
            BucketState oldValue = bucketState.get();
            BucketState newValue = calculateBucketState(oldValue, timeNow);
            if (newValue == null) {
                return false;
            }
            //CAS Retry
            if (bucketState.compareAndSet(oldValue, newValue)) {
                return true;
            }
        }
    }

    private BucketState calculateBucketState(BucketState bucketState, long timeNow) {
        long tokensToAdd = Math.max(((timeNow - bucketState.lastTimeStamp) * fillRate) / 1000, 0);

        long currentSize = Math.min(capacity, bucketState.currentSize + tokensToAdd);

        if (currentSize == 0) {
            return null;
        }
        return new BucketState(timeNow, currentSize - 1);
    }

}

public class LoggerApp {
}
