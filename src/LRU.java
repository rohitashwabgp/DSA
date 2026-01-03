//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//class Node {
//    Node next;
//    Node prev;
//    String val;
//    String key;
//
//    Node(String key, String val) {
//        this.key = key;
//        this.val = val;
//    }
//}
//
//public class LRU {
//    private Node head;
//    private Node tail;
//    ConcurrentMap<String, Node> LRUCache;
//    int capacity;
//    private final Object lock = new Object();
//
//    LRU(int capacity) {
//        this.capacity = capacity;
//        this.head = new Node("", "");
//        this.tail = new Node("", "");
//        this.head.next = this.tail;
//        this.tail.prev = this.head;
//        this.LRUCache = new ConcurrentHashMap<>();
//    }
//
//    String get(String key) {
//        if (!LRUCache.containsKey(key)) {
//            return null;
//        }
//        synchronized (lock) {
//            Node nodeToMove = LRUCache.get(key);
//            removeNode(nodeToMove);
//            addToTop(nodeToMove);
//            return nodeToMove.val;
//        }
//    }
//
//    private void addToTop(Node nodeToAdd) {
//        nodeToAdd.next = head.next;
//        nodeToAdd.next.prev = nodeToAdd;
//
//        nodeToAdd.prev = head;
//        nodeToAdd.prev.next = nodeToAdd;
//    }
//
//    private void removeNode(Node nodeToRemove) {
//        nodeToRemove.next.prev = nodeToRemove.prev;
//        nodeToRemove.prev.next = nodeToRemove.next;
//    }
//
//    void put(String key, String value) {
//        Node node = LRUCache.get(key);
//
//        if (node != null) {
//            node.val = value;
//            removeNode(node);
//            addToTop(node);
//            return;
//        }
//
//        if (LRUCache.size() == capacity) {
//            Node lru = tail.prev;
//            removeNode(lru);
//            LRUCache.remove(lru.key);
//        }
//
//        Node newNode = new Node(key, value);
//        LRUCache.put(key, newNode);
//        addToTop(newNode);
//    }
//
//}
