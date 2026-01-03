import java.util.HashMap;
import java.util.Map;

public class Datastructure {
    class Trie {
        Trie[] trie;
        boolean isEnd;

        public Trie() {
            trie = new Trie[26];
            isEnd = false;
        }

        void insert(String word) {
            Trie current = this;
            for (int i = 0; i < word.length(); i++) {
                int ch = word.charAt(i) - 'a';
                if (current.trie[ch] == null) {
                    current.trie[ch] = new Trie();
                }
                current = current.trie[ch];
            }
            current.isEnd = true;
        }

        boolean search(String word) {
            Trie current = this;
            for (int i = 0; i < word.length(); i++) {
                int ch = word.charAt(i) - 'a';
                if (current.trie[ch] == null) {
                    return false;
                }
                current = current.trie[ch];
            }
            return current.isEnd;
        }

        boolean startsWith(String prefix) {
            Trie current = this;
            for (int i = 0; i < prefix.length(); i++) {
                int ch = prefix.charAt(i) - 'a';
                if (current.trie[ch] == null) {
                    return false;
                }
                current = current.trie[ch];
            }
            return true;
        }
    }

    class Node {
        Node next, prev;
        String key, value;

        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    class LRUCache {
        Map<String, Node> cache;
        Node head;
        Node tail;
        int capacity;

        public LRUCache(int capacity) {
            cache = new HashMap<>();
            this.head = new Node("", "");
            this.tail = new Node("", "");
            this.head.next = this.tail;
            this.tail.prev = this.head;
            this.capacity = capacity;
        }

        String get(String key) {
            if (!cache.containsKey(key)) {
                return null;
            }
            Node findNode = cache.get(key);
            removeNode(findNode);
            addToTop(findNode);
            return findNode.value;
        }

        private void addToTop(Node findNode) {
            findNode.prev = head;
            findNode.next = head.next;
            head.next.prev = findNode;
            head.next = findNode;
        }

        void removeNode(Node nodeToRemove) {
            nodeToRemove.prev.next = nodeToRemove.next;
            nodeToRemove.next.prev = nodeToRemove.prev;
        }

        void put(String key, String value) {
            Node nodeToInsert;
            if (cache.containsKey(key)) {
                nodeToInsert = cache.get(key);
                nodeToInsert.value = value;
                removeNode(nodeToInsert);
            } else {
                nodeToInsert = new Node(key, value);
                if (capacity == cache.size()) {
                    Node lru = tail.prev;
                    removeNode(lru);
                    cache.remove(lru.key);
                }
                cache.put(key, nodeToInsert);
            }
            addToTop(nodeToInsert);
        }

    }

    class MinStack {
        int[] store;
        int initialCapacity;
        double incrementFactor;

        public MinStack(int initialCapacity, double incrementFactor) {
            this.initialCapacity = initialCapacity;
            this.store = new int[initialCapacity];
            this.incrementFactor = Math.max(incrementFactor, 1);
        }

        public MinStack() {
            this.initialCapacity = 50;
            this.store = new int[initialCapacity];
            this.incrementFactor = .75;
        }

        void push(int itemToPush) {

        }

        void pop(int itemToRemove) {

        }

        int top() {
            int itemAtBack = store[2];
            return itemAtBack;
        }

        int getMin() {
            int minItem = 0;
            return minItem;
        }


    }


    public static void main(String[] args) {

    }
}
