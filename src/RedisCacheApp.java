import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

interface RedisCache<T> {
    void set(String key, T value);

    void set(String key, T value, long ttlMillis);

    T get(String key);

    void del(String key);

    void expire(String key, long ttlMillis);
}

class CacheObject<T> {
    T value;

    public CacheObject(T value, String key, Long expiredAt) {
        this.value = value;
        this.key = key;
        this.expiredAt = expiredAt;
    }

    public CacheObject(T value, String key) {
        this.value = value;
        this.key = key;
    }

    String key;

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    Long expiredAt;
}

public class RedisCacheApp<T> implements RedisCache<T> {
    ConcurrentMap<String, CacheObject<T>> store = new ConcurrentHashMap<>();

    @Override
    public void set(String key, T value) {
        store.put(key, new CacheObject<T>(value, key));
    }

    @Override
    public void set(String key, T value, long ttlMillis) {
        long expireAt = System.currentTimeMillis() + ttlMillis;
        store.put(key, new CacheObject<T>(value, key, expireAt));
    }

    @Override
    public T get(String key) {
        CacheObject<T> returnObject = store.get(key);
        if (returnObject != null && isAlive(returnObject)) {
            return returnObject.value;
        }
        return null;
    }

    private static <T> boolean isAlive(CacheObject<T> returnObject) {
        return returnObject.expiredAt == null || returnObject.expiredAt > System.currentTimeMillis();
    }

    @Override
    public void del(String key) {
        store.remove(key);
    }

    @Override
    public void expire(String key, long ttlMillis) {
        long expireAt = System.currentTimeMillis() + ttlMillis;
        store.computeIfPresent(key, (k, oldValue) -> new CacheObject<>(oldValue.value, oldValue.key, expireAt));
    }
}

