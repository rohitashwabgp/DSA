import java.util.*;

public class CollectAllCoins {

    // Directions: up, down, left, right
    private static final int[][] DIRS = {{-1,0},{1,0},{0,-1},{0,1}};

    public int shortestPathAllCoins(String[] grid) {
        int m = grid.length;
        int n = grid[0].length();
        Map<Character, Integer> coinIndex = new HashMap<>(); // map coin letter to bit index
        int k = 0; // number of coins
        int startR = -1, startC = -1;

        // Step 1: scan grid to find coins and start
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                char ch = grid[r].charAt(c);
                if (ch >= 'a' && ch <= 'l') {
                    coinIndex.put(ch, k);
                    k++;
                } else if (ch == 'S') {
                    startR = r;
                    startC = c;
                }
            }
        }

        // Step 2: BFS with state = (row, col, mask)
        boolean[][][] visited = new boolean[m][n][1 << k];
        Queue<int[]> queue = new LinkedList<>();
        int startMask = 0;
        queue.offer(new int[]{startR, startC, startMask, 0}); // {r, c, mask, steps}
        visited[startR][startC][startMask] = true;

        int finalMask = (1 << k) - 1; // all coins collected

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1], mask = cur[2], steps = cur[3];

            // Check if we collected all coins
            if (mask == finalMask) return steps;

            for (int[] dir : DIRS) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (nr < 0 || nr >= m || nc < 0 || nc >= n) continue; // out of bounds
                char ch = grid[nr].charAt(nc);
                if (ch == '#') continue; // wall

                int newMask = mask;
                if (ch >= 'a' && ch <= 'l') {
                    newMask |= (1 << coinIndex.get(ch)); // pick up coin
                }

                if (!visited[nr][nc][newMask]) {
                    visited[nr][nc][newMask] = true;
                    queue.offer(new int[]{nr, nc, newMask, steps + 1});
                }
            }
        }

        return -1; // impossible to collect all coins
    }

    // Test
    public static void main(String[] args) {
        CollectAllCoins sol = new CollectAllCoins();

        String[] grid1 = {
                "S.a",
                ".#.",
                "..b"
        };
        System.out.println(sol.shortestPathAllCoins(grid1)); // Expected: 4

        String[] grid2 = {
                "S.#",
                "a#b",
                "...",
        };
        System.out.println(sol.shortestPathAllCoins(grid2)); // Expected: 6

        String[] grid3 = {
                "S#",
                "#a"
        };
        System.out.println(sol.shortestPathAllCoins(grid3)); // Expected: -1 (impossible)
    }
}
