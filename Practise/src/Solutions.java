import java.util.*;

public class Solutions {

    public long[] propagateMessages(int n, int[][] edges, long[] values, long k) {
        // build undirected graph
        List<Integer>[] g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        for (int[] e : edges) {
            g[e[0]].add(e[1]);
            g[e[1]].add(e[0]);
        }

        // root tree at 0 -> build parent[] and children[]
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        List<Integer>[] children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        Deque<Integer> dq = new ArrayDeque<>();
        boolean[] seen = new boolean[n];
        dq.add(0);
        seen[0] = true;
        parent[0] = -1;
        while (!dq.isEmpty()) {
            int u = dq.poll();
            for (int v : g[u]) {
                if (!seen[v]) {
                    seen[v] = true;
                    parent[v] = u;
                    children[u].add(v);
                    dq.add(v);
                }
            }
        }

        long[] cur = Arrays.copyOf(values, n);

        // cycle detection
        Map<String, Integer> seenStateToStep = new HashMap<>();
        List<long[]> states = new ArrayList<>();
        states.add(Arrays.copyOf(cur, n));
        seenStateToStep.put(Arrays.toString(cur), 0);

        for (long step = 0; step < k; step++) {
            // Phase 1: downward sends
            long[] addTo = new long[n];
            long[] lose = new long[n];

            for (int node = 0; node < n; node++) {
                int c = children[node].size();
                if (c == 0) continue;
                long val = cur[node];
                // simplified for binary-tree (and works for c==1 as well)
                long sendPerChild = val / 2; // floor(val/2)
                long totalSend = sendPerChild * (long) c;
                lose[node] += totalSend;
                for (int ch : children[node]) addTo[ch] += sendPerChild;
            }

            for (int i = 0; i < n; i++) cur[i] = cur[i] - lose[i] + addTo[i];

            // Phase 2: upward returns (children -> parent)
            long[] addToParent = new long[n];
            long[] loseChild = new long[n];
            for (int child = 0; child < n; child++) {
                int p = parent[child];
                if (p == -1) continue;
                long childVal = cur[child];
                if (childVal <= 0) continue;
                long ret = (childVal + 2) / 3; // ceil(childVal/3)
                loseChild[child] += ret;
                addToParent[p] += ret;
            }
            for (int i = 0; i < n; i++) cur[i] = cur[i] - loseChild[i] + addToParent[i];

            // cycle detection / store state
            String key = Arrays.toString(cur);
            int nextStepIndex = (int) (step + 1);
            if (seenStateToStep.containsKey(key)) {
                int firstOccur = seenStateToStep.get(key);
                int cycleLen = nextStepIndex - firstOccur;
                long remainingSteps = (k - nextStepIndex) % cycleLen;
                long[] ans = states.get(firstOccur + (int) remainingSteps);
                return Arrays.copyOf(ans, n);
            } else {
                states.add(Arrays.copyOf(cur, n));
                seenStateToStep.put(key, nextStepIndex);
            }

            // steady-state early stop
            long[] prev = states.get(states.size() - 2);
            if (Arrays.equals(prev, cur)) return Arrays.copyOf(cur, n);
        }

        return cur;
    }

    // example quick test
    public static void main(String[] args) {
        Solutions s = new Solutions();
        int n = 3;
        int[][] edges = new int[][]{{0,1},{0,2}};
        long[] values = new long[]{100,0,0};
        long k = 1;
        long[] res = s.propagateMessages(n, edges, values, k);
        System.out.println(Arrays.toString(res));
    }
}
