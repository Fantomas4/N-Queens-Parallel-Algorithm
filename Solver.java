import java.util.ArrayList;

public class Solver {
    int grid_size;
    ArrayList<Integer[]> results;

    public Solver(int grid_size) {
        this.grid_size = grid_size;
        this.results = new ArrayList<>();
    }

    public void startSolving() {
        this.solveQueens(0, new Integer[this.grid_size]);
    }

    public void solveQueens(int col, Integer[] rows) {
        if (col == this.grid_size) {
            // Found a valid n-queen solution
            this.results.add(rows.clone());
        } else {
            for (int row = 0; row < this.grid_size; row++) {
                if (checkValidity(rows, col, row)) {
                    rows[col] = row; // Place queen on the grid
                    solveQueens(col + 1, rows);
                }
            }
        }
    }

    // Check if (col1, row1) is a valid spot for a queen by checking if there is
    // a queen in the same row or diagonal. We do not need to check it for queens
    // in the same column because the calling solveQueens only attempts to place one
    // queen at a time. We know this column is empty.
    private boolean checkValidity(Integer[] rows, int col1, int row1) {
        for (int col2 = 0; col2 < col1; col2++) {
            int row2 = rows[col2];
            // check if (col2, row2) invalidates (col1, row1) as a queen
            // placement spot.

            // Check if columns have a queen in the same row
            if (row1 == row2) {
                return false;
            }

            // Check diagonals: If teh distance between the rows equals the
            // distance between the columns, then they are in the same diagonal.
            int rowDistance = Math.abs(row2 - row1);

            // col1 > col2, so no need for abs
            int colDistance = col1 - col2;
            if (rowDistance == colDistance) {
                return false;
            }
        }
        return true;
    }

    public void printSolution() {
        for (Integer[] row : this.results) {
            for (Integer elem : row) {
                System.out.print(elem);
            }
            System.out.println();
        }
    }
}




