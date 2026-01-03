import java.util.*;

class Node {
    Integer key;
    Integer val;
    Integer freq;

    Node(Integer key, Integer val) {
        this.key = key;
        this.val = val;
        this.freq = 1;
    }
}

public class LFUCache {
    Map<Integer, Node> cache;
    Map<Integer, LinkedHashSet<Node>> cacheCounter;
    int capacity;
    Integer minFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        this.minFreq = 0;
        this.cacheCounter = new HashMap<>();
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node != null) {
            updateFreq(node);
            return node.val;
        }
        return -1;
    }

    private void updateFreq(Node node) {
        LinkedHashSet<Node> counter = cacheCounter.get(node.freq);
        counter.remove(node);
        if (counter.isEmpty()) {
            cacheCounter.remove(node.freq);
            if (node.freq.equals(minFreq))  // ✅ safer than ==
                minFreq++;
        }
        node.freq++;
        cacheCounter.computeIfAbsent(node.freq, ignore -> new LinkedHashSet<>()).add(node);
    }

    public void put(int key, int value) {
        if (capacity == 0) return; // ✅ edge case

        Node node = cache.get(key);
        if (node != null) { // ✅ existing key: just update and bump freq
            node.val = value;
            updateFreq(node);
            return;
        }

        // ✅ evict LFU when full
        if (cache.size() >= capacity) {
            LinkedHashSet<Node> minSet = cacheCounter.get(minFreq);
            Node toRemove = minSet.iterator().next();
            minSet.remove(toRemove);
            if (minSet.isEmpty()) cacheCounter.remove(minFreq);
            cache.remove(toRemove.key);
        }

        // ✅ insert new node properly
        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        cacheCounter.computeIfAbsent(1, ignore -> new LinkedHashSet<>()).add(newNode);
        minFreq = 1;
    }
}
