import java.util.concurrent.*;

interface Cache {

    void put(String key, String value, long ttlMillis);

    String get(String key);

}

final class CacheInstance {
    final String key;
    final String value;
    final long ttl;
    final long timeStamp;

    CacheInstance(String key, String value, long ttl, long timeStamp) {
        this.key = key;
        this.value = value;
        this.ttl = ttl;
        this.timeStamp = timeStamp;
    }
}

class TTLCache implements Cache {

    ConcurrentMap<String, CacheInstance> store;

    ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    public TTLCache() {
        store = new ConcurrentHashMap<>();
        service.scheduleAtFixedRate(this::autoCleanUp, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public void put(String key, String value, long ttlMillis) {
        store.put(key, new CacheInstance(key, value, ttlMillis, System.currentTimeMillis()));
    }

    private void autoCleanUp() {
        long now = System.currentTimeMillis();

        store.forEach((key, instance) -> {
            if (instance.timeStamp + instance.ttl < now) {
                store.remove(key, instance); // remove only if unchanged
            }
        });
    }

    @Override
    public String get(String key) {
        CacheInstance instance = store.get(key);
        if (instance == null) return null;

        long now = System.currentTimeMillis();
        if (instance.timeStamp + instance.ttl < now) {
            store.remove(key, instance); // safe conditional remove
            return null;
        }
        return instance.value;
    }
}

class TTLCacheFactory {

}
