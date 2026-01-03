import java.util.Arrays;

public class Queens {
    public int totalNQueens(int n) {
        Character[][] board = new Character[n][n];
        for (int i = 0; i < board.length; i++) {
            Arrays.fill(board[i], '.');

        }
        return getCount(board, 0);

    }

    private int getCount(Character[][] board, int row) {
        int count = 0;
        if (row == board.length)
            return 1;
        for (int j = 0; j < board[row].length; j++) {
            if (isSafe(board, row, j)) {
                board[row][j] = 'Q';
                count = count + getCount(board, row + 1);
                board[row][j] = '.';
            }

        }
        return count;
    }

    private boolean isSafe(Character[][] board, int row, int col) {

        for (int i = 0; i < row; i++) {
            if (board[i][col] == 'Q')
                return false;
        }

        // Check upper-left diagonal
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 'Q')
                return false;
        }

        // Check upper-right diagonal
        for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) {
            if (board[i][j] == 'Q')
                return false;
        }

        return true;

    }
}

class Solution {
    public int totalNQueens(int n) {
        return solve(0, 0, 0, 0, n);
    }

    private int solve(int row, int cols, int diags, int antiDiags, int n) {
        if (row == n)
            return 1;

        int availablePositions = ((1 << n) - 1) & ~(cols | diags | antiDiags);

        int solutions = 0;
        while (availablePositions != 0) {
            int pos = availablePositions & -availablePositions;
            availablePositions &= availablePositions - 1;
            solutions += solve(row + 1, cols | pos, (diags | pos) << 1, (antiDiags | pos) >> 1, n);
        }

        return solutions;
    }
}


class SolutionOne {
    // Variable to store the total number of distinct solutions found
    private int count = 0;
    private int n;

    // Tracking structures for O(1) conflict checks
    private boolean[] cols;
    private boolean[] diag1; // r - c + n (shifted index)
    private boolean[] diag2; // r + c

    /**
     * Calculates the total number of distinct solutions to the N-Queens puzzle using backtracking.
     * Time Complexity: O(N!) (N factorial)
     * Space Complexity: O(N) for the tracking arrays and recursion stack.
     *
     * @param n The size of the chessboard.
     * @return The total number of solutions.
     */
    public int totalNQueens(int n) {
        this.n = n;
        this.cols = new boolean[n];
        // Diag1 and Diag2 need size 2*n because r-c ranges from -(n-1) to (n-1), and r+c ranges from 0 to 2*(n-1).
        this.diag1 = new boolean[2 * n]; 
        this.diag2 = new boolean[2 * n];

        // Start the backtracking process from the first row (row 0)
        backtrack(0);
        return count;
    }

    /**
     * Recursive backtracking function to try placing queens row by row.
     *
     * @param row The current row where we are attempting to place a queen.
     */
    private void backtrack(int row) {
        // Base Case: All n queens have been successfully placed
        if (row == n) {
            count++;
            return;
        }

        // Try placing a queen in every column of the current row
        for (int col = 0; col < n; col++) {
            // Calculate indices for the two diagonal types
            int diag1Index = row - col + n;
            int diag2Index = row + col;

            // Check if placement is safe (no conflicts in column, main diagonal, or anti-diagonal)
            if (!cols[col] && !diag1[diag1Index] && !diag2[diag2Index]) {
                
                // 1. Choose (Place the queen virtually by marking tracking arrays)
                cols[col] = true;
                diag1[diag1Index] = true;
                diag2[diag2Index] = true;

                // 2. Recurse (Move to the next row)
                backtrack(row + 1);

                // 3. Unchoose (Backtrack - undo the markings)
                cols[col] = false;
                diag1[diag1Index] = false;
                diag2[diag2Index] = false;
            }
        }
    }
}
