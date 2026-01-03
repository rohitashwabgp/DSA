import java.util.ArrayDeque;
import java.util.Deque;

public class WordSearch {
    class State {
        int row;
        int col;
        String currentWord;

        public State(int row, int col, String currentWord) {
            this.row = row;
            this.col = col;
            this.currentWord = currentWord;
        }
    }

    public boolean exist(char[][] board, String word) {
        int[][] directions = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {0, 1}, {1, 0}, {-1, 0}, {0, -1}};
        boolean[][] visited = new boolean[board.length][board[0].length];
        Deque<State> queue = new ArrayDeque<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                queue.push(new State(i, j, String.valueOf(board[i][j])));
            }
        }

        while (!queue.isEmpty()) {
            State current = queue.poll();
            int row = current.row;
            int col = current.col;
            String currentWord = current.currentWord;
            if (currentWord.equals(word)) return true;
            if (!word.contains(currentWord)) continue;
            for (int[] direction : directions) {
                int dx = row + direction[0];
                int dy = col + direction[1];
                if (dx < 0 || dy < 0 || dx > board.length || dy > board[0].length) continue;

                if (!visited[dx][dy]) {
                    visited[dx][dy] = true;
                    queue.push(new State(dx, dy, currentWord.concat(String.valueOf(board[dx][dy]))));
                }
            }
        }
        return false;
    }

    private static void main(String[] args) {

    }
}
