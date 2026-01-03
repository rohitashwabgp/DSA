private static List<Integer> outTree(int[] input, int[][] edges) {
    List<Integer> result = new ArrayList<>();
    List<Integer>[] adjacent = new List[input.length];
    if (edges.length == 0) return result;
    for (int i = 0; i < input.length; i++) adjacent[i] = new ArrayList<>();
    for (int[] edge : edges) {
        adjacent[edge[0]].add(edge[1]);
        adjacent[edge[1]].add(edge[0]);
    }
    int[] down = new int[input.length];
    int[] up = new int[input.length];
    up[0] = 0;
    int total = downCalculation(0, -1, adjacent, down, input);
    upCalculation(0, -1, adjacent, up, down);
    for (int curr : up) {
        result.add(curr);
    }
    return result;
}

private static void upCalculation(int idx, int parent, List<Integer>[] adjacent, int[] up, int[] down) {

    for (int child : adjacent[idx]) {
        if (child == parent) continue;
        up[child] = up[idx] + (down[idx] - down[child]);
        upCalculation(child, idx, adjacent, up, down);
    }
}

private static int downCalculation(int i, int parent, List<Integer>[] adjacent, int[] down, int[] input) {
    int currentSum = input[i];
    for (int child : adjacent[i]) {
        if (child == parent) continue;
        downCalculation(child, i, adjacent, down, input);
        currentSum = currentSum + down[child];
    }
    down[i] = currentSum;
    return currentSum;
}

private static boolean subArraySum(int[] num, int target) {
    boolean[] visit = new boolean[num.length];
    return backTrack(num, target, 0, visit);

}

private static boolean backTrack(int[] num, int target, int index, boolean[] visit) {
    if (target == 0) return true;
    for (int i = index; i < num.length; i++) {
        if (visit[i]) continue;
        visit[i] = true;
        target = target - num[i];
        boolean result = backTrack(num, target, i + 1, visit);
        if (result) return true;
        visit[i] = false;
        target = target + num[i];
    }
    return false;
}

private static List<Integer> maxmin(int[] nums, int maxSize) {
    int left = 0;
    Deque<Integer> maxHeap = new ArrayDeque<>();
    Deque<Integer> minHeap = new ArrayDeque<>();
    List<Integer> result = new ArrayList<>();
    for (int right = 0; right < nums.length; right++) {
        int current = nums[right];
        while (!minHeap.isEmpty() && nums[minHeap.peekLast()] > current) {
            minHeap.pollLast();
        }
        while (!maxHeap.isEmpty() && nums[maxHeap.peekLast()] < current) {
            maxHeap.pollLast();
        }
        maxHeap.offer(right);
        minHeap.offer(right);
        if (right - left + 1 == maxSize) {
            result.add(nums[maxHeap.peek()] - nums[minHeap.peek()]);
            if (maxHeap.peek() == left) maxHeap.pollFirst();
            if (minHeap.peek() == left) minHeap.pollFirst();
            left++;
        }
    }
    return result;
}

public static int maxScore(int[] nums) {
    int n = nums.length;
    int[] left = new int[n];
    int[] right = new int[n];
    Stack<Integer> stack = new Stack<>();

    // Compute left boundaries
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[stack.peek()] >= nums[i]) {
            stack.pop();
        }
        left[i] = stack.isEmpty() ? -1 : stack.peek();
        stack.push(i);
    }

    stack.clear();

    // Compute right boundaries
    for (int i = n - 1; i >= 0; i--) {
        while (!stack.isEmpty() && nums[stack.peek()] >= nums[i]) {
            stack.pop();
        }
        right[i] = stack.isEmpty() ? n : stack.peek();
        stack.push(i);
    }

    // Calculate max score
    int maxScore = 0;
    for (int i = 0; i < n; i++) {
        int length = right[i] - left[i] - 1;
        int score = nums[i] * length;
        maxScore = Math.max(maxScore, score);
    }

    return maxScore;
}


private static int lis(String s) {
    if (s.isEmpty() || s.length() == 1) return s.length();
    int left = 0;
    int[] freqMap = new int[256];
    int maxLength = 0;
    for (int right = 0; right < s.length(); right++) {
        int current = s.charAt(right);
        freqMap[current]++;
        while (left < right && freqMap[current] > 1) {
            int leftCh = s.charAt(left);
            freqMap[leftCh]--;
            left++;
        }
        maxLength = Math.max(maxLength, right - left + 1);
    }
    return maxLength;
}

private static List<Integer> kFrequent(int[] nums, int k) {
    if (k > nums.length) return new ArrayList<>();
    Map<Integer, Integer> freqMap = new HashMap<>();
    PriorityQueue<Map.Entry<Integer, Integer>> queueMap = new PriorityQueue<>((first, second) -> first.getValue().compareTo(second.getValue()));

    for (int num : nums) {
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
    }

    for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
        queueMap.offer(entry);
        if (queueMap.size() > k) {
            queueMap.poll();
        }
    }
    List<Integer> result = new ArrayList<>();
    for (Map.Entry<Integer, Integer> entry : queueMap) {
        result.add(entry.getKey());
    }
    return result;
}

private static int islands(char[][] grid) {
    int count = 0;
    if (grid.length == 0) return count;
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == '1') {
                count++;
                dfs(i, j, grid);
            }
        }
    }
    return count;
}

private static void dfs(int row, int col, char[][] grid) {
    int[][] directions = new int[][]{{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    if (col >= grid[0].length || col < 0 || row < 0 || row >= grid.length || grid[row][col] == '0' || grid[row][col] == 'x')
        return;
    if (grid[row][col] == '1') grid[row][col] = 'x';
    for (int[] direction : directions) {
        dfs(row + direction[0], col + direction[1], grid);
    }
}

private static boolean coursePrerequisite(int[][] prerequisites, int n) {
    List<Integer>[] adjacentCourses = new List[n];
    for (int i = 0; i < n; i++) adjacentCourses[i] = new ArrayList<>();
    //  int i = 0;
    int[] indegree = new int[n];
    boolean[] visited = new boolean[n];
    Deque<Integer> queue = new ArrayDeque<>();
    for (int[] course : prerequisites) {
        adjacentCourses[course[1]].add(course[0]);
        indegree[course[0]]++;
    }
    for (int i = 0; i < n; i++) {
        if (indegree[i] == 0) {
            queue.offer(i);
            visited[i] = true;
        }
    }
    int currentCount = 0;

    while (!queue.isEmpty()) {
        int current = queue.poll();
        currentCount++;
        for (Integer next : adjacentCourses[current]) {
            indegree[next]--;
            if (indegree[next] == 0 && !visited[next]) {
                queue.offer(next);
            }
        }
    }
    return currentCount == n;
}

private static void wallsAndGates(int[][] rooms) {
    int[][] directions = new int[][]{{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    boolean[][] visited = new boolean[rooms.length][rooms[0].length];
    for (int row = 0; row < rooms.length; row++) {
        for (int col = 0; col < rooms[0].length; col++) {
            if (rooms[row][col] == 0) {
                dfs2(row, col, rooms, directions, 0, visited);
            }
        }
    }
}

private static void dfs2(int row, int col, int[][] rooms, int[][] directions, int count, boolean[][] visited) {
    if (row < 0 || col < 0 || row >= rooms.length || col >= rooms[0].length || rooms[row][col] == -1 || visited[row][col])
        return;
    if (rooms[row][col] != 0 || rooms[row][col] != -1) {
        rooms[row][col] = Math.min(count, rooms[row][col]);
    }
    for (int[] direction : directions) {
        visited[row][col] = true;
        dfs2(row + direction[0], col + direction[1], rooms, directions, count + 1, visited);
        visited[row][col] = false;
    }
}

//    static class State {
//        int[] data;
//        boolean[][] visited;
//
//        public State(int[] data, boolean[][] visited) {
//            this.data = data;
//            this.visited = visited;
//        }
//    }

private static void wallsAndGatesBFS(int[][] rooms) {
    int[][] directions = new int[][]{{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
    Deque<int[]> queue = new ArrayDeque<>();
    for (int row = 0; row < rooms.length; row++) {
        for (int col = 0; col < rooms[0].length; col++) {
            if (rooms[row][col] == 0) {
                queue.offer(new int[]{row, col});
            }
        }
    }
    while (!queue.isEmpty()) {
        int[] current = queue.poll();
        int row = current[0];
        int col = current[1];


        for (int[] direction : directions) {
            if (row + direction[0] < 0 || col + direction[1] < 0 || row + direction[0] >= rooms.length || col + direction[1] >= rooms[0].length || rooms[row + direction[0]][col + direction[1]] != Integer.MAX_VALUE) {
                continue;
            }
            rooms[row + direction[0]][col + direction[1]] = rooms[row][col] + 1;
            queue.offer(new int[]{row + direction[0], col + direction[1]});

        }
    }
}

private static void wallsAndGatesBFS2(int[][] rooms) {
    if (rooms == null || rooms.length == 0) return;

    int rows = rooms.length;
    int cols = rooms[0].length;
    int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};

    Deque<int[]> queue = new ArrayDeque<>();

    // 1. Enqueue all gates (multi-source)
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (rooms[r][c] == 0) {
                queue.offer(new int[]{r, c});
            }
        }
    }

    // 2. BFS
    while (!queue.isEmpty()) {
        int[] cell = queue.poll();
        int row = cell[0];
        int col = cell[1];

        for (int[] d : directions) {
            int nr = row + d[0];
            int nc = col + d[1];

            // boundary + only process empty rooms
            if (nr < 0 || nc < 0 || nr >= rows || nc >= cols || rooms[nr][nc] != Integer.MAX_VALUE) {
                continue;
            }

            rooms[nr][nc] = rooms[row][col] + 1;
            queue.offer(new int[]{nr, nc});
        }
    }
}

private static int largestRectangleHistogram(int[] heights) {
    if (heights.length == 0) return 0;
    int maxArea = 0;
    int[] left = new int[heights.length];
    int[] right = new int[heights.length];
    Stack<Integer> stack = new Stack<>();
    for (int i = 0; i < heights.length; i++) {
        while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
            stack.pop();
        }
        left[i] = stack.isEmpty() ? -1 : stack.peek();
        stack.push(i);
    }
    stack.clear();

    for (int i = heights.length - 1; i >= 0; i--) {
        while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
            stack.pop();
        }
        right[i] = stack.isEmpty() ? heights.length : stack.peek();
        stack.push(i);
    }
    stack.clear();

    for (int i = 0; i < heights.length; i++) {
        maxArea = Math.max(maxArea, (right[i] - left[i] - 1) * heights[i]);
    }

    return maxArea;
}

private static int largestRectangleHistogram2(int[] heights) {
    if (heights.length == 0) return 0;
    Stack<Integer> histoStack = new Stack<>();
    int maxArea = 0;

    for (int i = 0; i <= heights.length; i++) {
        int current = i == heights.length ? 0 : heights[i];
        while (!histoStack.isEmpty() && heights[histoStack.peek()] >= current) {
            int last = heights[histoStack.pop()];
            int width = histoStack.isEmpty() ? i : i - histoStack.peek() - 1;
            maxArea = Math.max(maxArea, last * width);
        }
        histoStack.push(i);
    }
    return maxArea;
}

private static int calculateScore(int[] nums, int k) {
    int[] left = new int[nums.length];
    int[] right = new int[nums.length];
    Stack<Integer> store = new Stack<>();
    int score = 0;
    for (int i = 0; i < nums.length; i++) {
        while (!store.isEmpty() && nums[store.peek()] >= nums[i]) {
            store.pop();
        }
        left[i] = store.isEmpty() ? -1 : store.peek();
        store.push(i);
    }
    store.clear();
    for (int i = nums.length - 1; i >= 0; i--) {
        while (!store.isEmpty() && nums[store.peek()] >= nums[i]) {
            store.pop();
        }
        right[i] = store.isEmpty() ? nums.length : store.peek();
        store.push(i);
    }
    store.clear();
    // Check all valid minimums that include k
    for (int i = 0; i < nums.length; i++) {
        if (left[i] < k && k < right[i]) {
            int length = right[i] - left[i] - 1;
            score = Math.max(score, nums[i] * length);
        }
    }
    return score;

}

private static int minimumValue2(int[] array) {
    int n = array.length;
    long MOD = 1_000_000_007;
    long MOD2 = (long) 1e9 + 7;
    long total = 0;

    int[] left = new int[n];
    int[] right = new int[n];
    Stack<Integer> stack = new Stack<>();

    // Previous smaller (strict)
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && array[stack.peek()] > array[i]) {
            stack.pop();
        }
        left[i] = stack.isEmpty() ? -1 : stack.peek();
        stack.push(i);
    }

    stack.clear();

    // Next smaller or equal
    for (int i = n - 1; i >= 0; i--) {
        while (!stack.isEmpty() && array[stack.peek()] >= array[i]) {
            stack.pop();
        }
        right[i] = stack.isEmpty() ? n : stack.peek();
        stack.push(i);
    }

    for (int i = 0; i < n; i++) {
        long contribution = (long) (i - left[i]) * (right[i] - i) % MOD * array[i] % MOD;

        total = (total + contribution) % MOD;
    }

    return (int) total;
}


private static int minimumValue(int[] array) {
    if (array.length == 0) return 0;
    int total = 0;
    int[] left = new int[array.length];
    int[] right = new int[array.length];
    Stack<Integer> stack = new Stack<>();
    for (int i = 0; i < array.length; i++) {
        while (!stack.isEmpty() && array[stack.peek()] >= array[i]) {
            stack.pop();
        }
        left[i] = stack.isEmpty() ? -1 : stack.peek();
        stack.push(i);
    }
    stack.clear();

    for (int i = array.length - 1; i >= 0; i--) {
        while (!stack.isEmpty() && array[stack.peek()] >= array[i]) {
            stack.pop();
        }
        right[i] = stack.isEmpty() ? array.length : stack.peek();
        stack.push(i);
    }
    stack.clear();

    for (int i = 0; i < array.length; i++) {
        total = total + ((right[i] - i) * (i - left[i]) * array[i]);
    }

    return total;
}

private static int singleNumber(int[] nums) {
    int result = 0;
    for (int i = 0; i < 32; i++) { // each bit
        int bitCount = 0;
        for (int num : nums) {
            if (((num >> i) & 1) == 1) {
                bitCount++;
            }
        }
        if (bitCount % 3 != 0) {
            result |= (1 << i); // set the i-th bit
        }
    }
    return result;
}

private static int singleNumber2(int[] nums) {
    int result = 0;
    for (int i = 0; i < 32; i++) {
        int contribution = 0;
        for (int num : nums) {
            if (((num >> i) & 1) == 1) {
                contribution++;
            }

            if (contribution % 2 != 0) {
                result = result | (1 << i);
            }
        }
    }
    return result;
}


private static int[] singleNumberIII(int[] nums) {
    // Step 1: XOR all numbers → x ^ y
    int xor = 0;
    for (int num : nums) {
        xor ^= num;
    }

    // Step 2: Find any set bit in xor (rightmost set bit)
    int diffBit = xor & -xor; // isolates rightmost 1-bit

    // Step 3: Separate numbers into two groups and XOR
    int num1 = 0, num2 = 0;
    for (int num : nums) {
        if ((num & diffBit) == 0) {
            num1 ^= num; // group 1
        } else {
            num2 ^= num; // group 2
        }
    }

    return new int[]{num1, num2};
}

private static int findLeftmostPeak(int[] nums) {
    int low = 0, high = nums.length - 1;

    while (low < high) {
        int mid = low + (high - low) / 2;

        // If mid < mid+1, peak is on the right
        if (nums[mid] < nums[mid + 1]) {
            low = mid + 1;
        } else {
            // mid >= mid+1, peak could be mid (or left)
            high = mid;
        }
    }

    // low == high is leftmost peak
    return low;
}


void main() {


    //  int[] nums = {5, 5, 4, 5, 5};
    int k = 2;
    // System.out.println(calculateScore(nums, k));
    int[] heights = {3, 1, 2, 4};
    // System.out.println(minimumValue2(heights));
    int[] nums = new int[]{1, 2, 2, 3, 3, 2, 1};
    IO.println(findLeftmostPeak(nums));
}
