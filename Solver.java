import java.util.ArrayList;

public class Solver {
    int grid_size;
    ArrayList<Integer[]> results = new ArrayList<>();
    int threadsNumber;
    Thread[] threads;
    private final Object lock = new Object();

    public Solver(int grid_size) {
        this.grid_size = grid_size;
        this.threadsNumber = grid_size;
        this.threads = new Thread[threadsNumber];

    }

    public void startSolving() {
        for (int i = 0; i < threadsNumber; i++) {
            final int row = i;
            this.threads[i] = new Thread(() -> {
                Integer[] rows = new Integer[this.grid_size];
                rows[0] = row;
                solveQueens(1, rows);
            });
            this.threads[i].start();
        }

        for (int i = 0; i < threadsNumber; i++) {
            try {
                this.threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void solveQueens(int col, Integer[] rows) {
        System.out.println("SOLVEQUEENS -> ENTERED!");
        if (col == this.grid_size) {
            // Found a valid n-queen solution
            synchronized(this.lock) {
                System.out.println("*** SOLUTION SAVED!");
                this.results.add(rows.clone());
            }
        } else {
            for (int row = 0; row < this.grid_size; row++) {
                if (checkValidity(rows, col, row)) {
                    System.out.println("MPIKA1!");
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

    public static void main(String[] args) {
        Solver solver = new Solver(4);
        solver.startSolving();
        solver.printSolution();
    }

}




