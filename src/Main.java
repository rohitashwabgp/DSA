static class Node {
    Node next;
    Node prev;
    String val;
    String key;
    int freq;

    Node(String key, String val, int freq) {
        this.key = key;
        this.val = val;
        this.freq = freq;
    }

}

static class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}

Function<TreeNode, String> serialize = (root) -> {
    StringBuilder sb = new StringBuilder();
    serializeHelper(root, sb);
    return sb.toString();
};

void serializeHelper(TreeNode node, StringBuilder sb) {
    if (node == null) {
        sb.append("null,");
        return;
    }

    sb.append(node.val).append(",");
    serializeHelper(node.left, sb);
    serializeHelper(node.right, sb);
}

Function<String, TreeNode> deSerialize = (treeString) -> {
    String[] values = treeString.split(",");
    Queue<String> queue = new LinkedList<>(Arrays.asList(values));
    return deserializeHelper(queue);
};

private TreeNode deserializeHelper(Queue<String> queue) {
    String val = queue.poll();
    if (val != null && val.equals("null")) {
        return null;
    }
    TreeNode node = new TreeNode(Integer.parseInt(val));
    node.left = deserializeHelper(queue);
    node.right = deserializeHelper(queue);
    return node;
}


static class LRUCache {
    Node head;
    Node tail;
    Map<String, Node> cache;
    int capacity;

    LRUCache(int capacity) {
        cache = new HashMap<>();
        head = new Node("", "", 1);
        tail = new Node("", "", 1);
        head.next = tail;
        tail.prev = head;
        this.capacity = capacity;
    }

    String get(String key) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            remove(node);
            addToTop(node);
            return node.val;
        }
        return null;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addToTop(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    void put(String key, String value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.val = value;
            remove(node);
            addToTop(node);
            return;
        }
        if (capacity == cache.size()) {
            Node lru = tail.prev;
            remove(lru);
            cache.remove(lru.key);
        }
        Node node = new Node(key, value, 1);
        addToTop(node);
        cache.put(key, node);
    }
}

class MFUCache {


    int capacity;
    int maxFreq = 0;

    Map<String, Node> cache = new HashMap<>();
    Map<Integer, LinkedHashSet<Node>> freqMap = new HashMap<>();

    MFUCache(int capacity) {
        this.capacity = capacity;
    }

    public String get(int key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        Node node = cache.get(key);
        updateFreq(node);
        return node.val;
    }

    private void updateFreq(Node node) {
        int freq = node.freq;
        freqMap.get(freq).remove(node);
        node.freq++;
        freqMap.computeIfAbsent(node.freq, (h) -> new LinkedHashSet<>()).add(node);
        maxFreq = Math.max(node.freq, maxFreq);
    }

//    public List<Integer> dijKastra(int[][] edges) {
//        List<Integer>[] adjacent = new List[edges.length];
//    }

    public void put(String key, String value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.val = value;
            updateFreq(node);
            return;
        }
        if (cache.size() == capacity) {
            LinkedHashSet<Node> hashSet = freqMap.get(maxFreq);
            Node evict = hashSet.iterator().next();
            hashSet.remove(evict);
            cache.remove(evict.key);
        }

        Node node = new Node(key, value, 1);
        cache.put(key, node);
        freqMap.computeIfAbsent(1, f -> new LinkedHashSet<>()).add(node);
        maxFreq = Math.max(maxFreq, 1);

    }
}


Function<String, Integer> longestSubString = (input) -> {
    int left = 0;
    int length = 0;
    Map<Character, Integer> freqMap = new HashMap<>();

    for (int right = 0; right < input.length(); right++) {
        char rightCh = input.charAt(right);
        freqMap.put(rightCh, freqMap.getOrDefault(rightCh, 0) + 1);

        while (left < right && freqMap.get(rightCh) > 1) {
            char leftCh = input.charAt(left);
            freqMap.put(leftCh, freqMap.get(leftCh) - 1);
            if (freqMap.get(leftCh) == 0) freqMap.remove(leftCh);
            left++;
        }
        length = Math.max(length, right - left + 1);
    }
    return length;
};

Function<int[], Integer> trappingWater = (input) -> {
    int total = 0;
    Stack<Integer> indexStore = new Stack<>();
    for (int i = 0; i < input.length; i++) {
        while (!indexStore.isEmpty() && input[indexStore.peek()] < input[i]) {
            int height = indexStore.pop();
            if (indexStore.isEmpty()) break;
            int width = i - indexStore.peek() - 1;
            int boundedHeight = Math.min(input[i], input[indexStore.peek()]) - height;
            total += boundedHeight * width;
        }
    }
    return total;
};

Function<int[][], int[][]> mergeInterval = (input) -> {
    int[] current = input[0];
    Arrays.sort(input, Comparator.comparingInt(a -> a[0]));
    List<int[]> result = new ArrayList<>();
    for (int i = 1; i < input.length; i++) {
        if (current[1] >= input[i][0]) {
            current[0] = Math.min(current[0], input[i][0]);
            current[1] = Math.max(current[1], input[i][1]);
        } else {
            result.add(current);
            current = input[i];
        }
    }
    result.add(current);
    return result.toArray(new int[result.size()][2]);
};

Function<int[], int[]> maximumK = (input) -> {
    int k = 3;
    Deque<Integer> queue = new ArrayDeque<>();
    int left = 0;
    int[] result = new int[input.length - k + 1];
    for (int right = 0; right < input.length; right++) {
        if (!queue.isEmpty() && queue.peekFirst() < left) {
            queue.pollFirst();
        }
        while (!queue.isEmpty() && input[queue.peekLast()] < input[right]) {
            queue.pollLast();
        }
        queue.addLast(right);
        if (right - left + 1 == k) {
            result[left] = input[queue.peekFirst()];
            left++;
        }
    }
    return result;
};

public static int quickSelect(int[] arr, int k) {
    return quickHelper(arr, 0, arr.length - 1, k);
}

private static int quickHelper(int[] arr, int left, int right, int k) {
    if (left == right) return arr[left];
    int pivotIndex = partition(arr, left, right);
    if (pivotIndex == k) return arr[pivotIndex];
    else if (pivotIndex < k) return quickHelper(arr, left, pivotIndex - 1, k);
    else return quickHelper(arr, pivotIndex + 1, right, k);
}


private static int partition(int[] arr, int left, int right) {
    int pivot = arr[right];
    int i = left;

    for (int j = left; j < right; j++) {
        if (arr[j] <= pivot) {
            swap(arr, i, j);
            i++;
        }
    }
    swap(arr, i, right);
    return i;
}

private static void swap(int[] arr, int left, int right) {
    int temp = arr[left];
    arr[right] = arr[left];
    arr[left] = temp;
}

public int[] maxSlidingWindow(int[] nums, int k) {
    if (nums == null || k == 0) return new int[0];

    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> dq = new ArrayDeque<>();

    for (int i = 0; i < n; i++) {

        if (!dq.isEmpty() && dq.peekFirst() == i - k) {
            dq.pollFirst();
        }
        while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) {
            dq.pollLast();
        }

        dq.offerLast(i);
        if (i >= k - 1) {
            result[i - k + 1] = nums[dq.peekFirst()];
        }
    }
    return result;
}


boolean canForm(String s, int index, Set<String> dict, Boolean[] memo) {
    if (index == s.length()) return true;
    if (memo[index] != null) return memo[index];

    for (String word : dict) {
        if (s.startsWith(word, index)) {
            if (canForm(s, index + word.length(), dict, memo)) {
                return memo[index] = true;
            }
        }
    }
    return memo[index] = false;
}

private boolean canForm2(int index, String word, Set<String> dictionary) {
    if (index >= word.length()) return true;
    for (String each : dictionary) {
        if (word.startsWith(each, index)) {
            if (canForm2(index + each.length(), word, dictionary)) return true;
        }
    }
    return false;
}


void main() {
    Set<String> wordset = new HashSet<>();
    wordset.add("er");
    wordset.add("pr");
    wordset.add("ff");
    wordset.add("k");
    System.out.println(canForm2(0, "prkff", wordset));
    int[] input = new int[]{1, 3, -1, -3, 5, 3, 6, 7};
    IO.println(Arrays.toString(maximumK.apply(input)));
}


