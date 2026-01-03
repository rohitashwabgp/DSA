static List<Integer>[] adj;
static long[] down; // subtree sums
static long[] up;   // sum of values outside subtree
static int n;
static List<Integer> values;

// DFS1: compute down[u] = sum of subtree rooted at u
static void dfs1(int u, int parent) {
    long sum = values.get(u);
    for (int v : adj[u]) {
        if (v == parent) continue;
        dfs1(v, u);
        sum += down[v];
    }
    down[u] = sum;
}

// DFS2 (reroot): compute up[v] for children v using up[u] and down[]
// Formula: up[v] = up[u] + (down[u] - down[v])
static void dfs2(int u, int parent) {
    for (int v : adj[u]) {
        if (v == parent) continue;
        up[v] = up[u] + (down[u] - down[v]);
        dfs2(v, u);
    }
}

public static List<Long> transformTreeValuesReroot(int nn, List<int[]> edges, List<Integer> vals) {
    n = nn;
    values = vals;
    adj = new ArrayList[n];
    for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
    for (int[] e : edges) {
        adj[e[0]].add(e[1]);
        adj[e[1]].add(e[0]);
    }

    down = new long[n];
    up = new long[n];

    // pick 0 as root (tree assumed connected)
    dfs1(0, -1);

    up[0] = 0;           // root has no outside-subtree sum
    dfs2(0, -1);

    List<Long> result = new ArrayList<>(n);
    for (int i = 0; i < n; i++) result.add(down[i] + up[i]);
    return result;
}

// test driver
void main() {
    int n = 4;
    List<int[]> edges = Arrays.asList(new int[]{0, 1}, new int[]{0, 2}, new int[]{1, 3});
    List<Integer> vals = Arrays.asList(1, 2, 3, 4);

    List<Long> ans = transformTreeValuesReroot(n, edges, vals);
    IO.println("down: " + Arrays.toString(down)); // [10,6,3,4]
    IO.println("up:   " + Arrays.toString(up));   // [0,4,7,6]
    IO.println("res:  " + ans);                  // [10,10,10,10]
}
