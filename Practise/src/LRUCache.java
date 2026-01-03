import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

class Node {
    String key, value;
    Node prev, next;

    Node(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

public class LRUCache {
    Map<String, Node> store;
    Node head;
    Node tail;
    int capacity;

    public LRUCache(int capacity) {
        this.store = new HashMap<>();
        this.capacity = capacity;
        head = new Node("", "");
        tail = new Node("", "");
        head.next = tail;
        tail.prev = head;
    }

    String get(String key) {
        if (!store.containsKey(key)) {
            throw new NoSuchElementException("No element with key: ".concat(key));
        }
        Node item = store.get(key);
        removeNode(item);
        addToTop(item);
        return item.value;
    }

    private void removeNode(Node item) {
        item.prev.next = item.next;
        item.next.prev = item.prev;
    }

    private void addToTop(Node item) {
        item.next = head.next;
        item.prev = head;
        head.next.prev = item;
        head.next = item;
    }

    void put(String key, String value) {
        if (store.containsKey(key)) {
            removeNode(store.get(key));
        }
        Node node = new Node(key, value);
        addToTop(node);
        store.put(key, node);
        if (this.store.size() > capacity) {
            Node lru = tail.prev;
            removeNode(lru);
            store.remove(lru.key);
        }
    }

}
