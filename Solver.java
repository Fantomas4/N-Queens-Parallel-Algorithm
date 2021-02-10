import java.util.ArrayList;
import java.util.Scanner;

public class Solver {
    int grid_size; // The size of the grid to be used (number "n" of the n-queens problem).
    ArrayList<Integer[]> results = new ArrayList<>(); // Used to store the solutions found for the given n-queens problem.
    int threadsNumber; // The number of threads that should be used in finding a solution to the n-queens problem.
    Thread[] threads; // A list containing all the threads used in the problem solving procedure.
    private final Object lock = new Object(); // A lock used to synchronize access between threads to the results ArrayList.

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
        if (col == this.grid_size) {
            // Found a complete n-queen solution
            synchronized(this.lock) {
                this.results.add(rows.clone());
            }
        } else {
            // Continue looking for a solution recursively
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
            // Check if (col2, row2) invalidates (col1, row1) as a queen
            // placement spot.
            int row2 = rows[col2];

            // Check if columns have a queen in the same row
            if (row1 == row2) {
                return false;
            }

            // Check diagonals: If the distance between the rows equals the
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

    public void printSolutionGrid() {
        System.out.printf("%n> There are %d solutions to the %d-Queens problem:%n%n", this.results.size(), this.grid_size);

        // The size for the grid used to print the results,
        // taking the additional space needed for border characters
        // into account.
        int printGridSize = this.grid_size + 3;

        int solCounter = 0; // Counter used to keep track of the solution
        // currently being printed

        // Print a grid containing the queens positions
        // for each n-queens solution found
        for (Integer[] solution : this.results) {
            solCounter++;
            System.out.printf(" Solution %d:%n", solCounter);

            String[][] grid = new String[printGridSize][printGridSize];

            // Initialize all grid positions with ""
            for (int i = 0; i < printGridSize; i++) {
                for (int j = 0; j < printGridSize; j++) {
                    grid[i][j] = "";
                }
            }

            // Set reference numbers at the left border of the grid
            for (int i = 1; i < printGridSize - 2; i++) {
                grid[i][0] = Integer.toString(this.grid_size - i);
            }

            // Set reference numbers at the bottom border of the grid
            grid[printGridSize - 1][0] = " ";
            grid[printGridSize - 1][1] = " ";
            for (int j = 2; j < printGridSize - 1; j++) {
                grid[printGridSize - 1][j] = String.format(" %d ", j - 2);
            }

            // Initialize all possible queen positions with .
            for (int i = 1; i < printGridSize - 1; i++) {
                for (int j = 2; j < printGridSize; j++) {
                    grid[i][j] = " . ";
                }
            }

            // Set the + symbol at the grid corners
            grid[0][1] = " +";
            grid[0][printGridSize - 1] = "+";
            grid[printGridSize - 2][1] = " +";
            grid[printGridSize - 2][printGridSize - 1] = "+";

            // Set the - symbol at the top horizontal grid border
            for (int j = 2; j < printGridSize - 1; j++) {
                grid[0][j] = " - ";
            }

            // Set the - symbol at the bottom horizontal grid border
            for (int j = 2; j < printGridSize - 1; j++) {
                grid[printGridSize - 2][j] = " - ";
            }

            // Set the | symbol at the left vertical grid border
            for (int i = 1; i < printGridSize - 2; i++) {
                grid[i][1] = "|";
            }

            // Set the | symbol at the right vertical grid border
            for (int i = 1; i < printGridSize - 2; i++) {
                grid[i][printGridSize - 1] = "|";
            }

            // Add the queens positions to the grid
            for (int j = 0; j < solution.length; j++) {
                grid[solution[j] + 1][j + 2] = " X ";
            }

            // Print the generated grid
            for (int i = 0; i < printGridSize; i++) {
                for (int j = 0; j < printGridSize; j++) {
                    System.out.print(grid[i][j]);
                }
                System.out.println();
            }

            System.out.print("\n\n");
        }
    }

    public static void main(String[] args) {
        while (true) {
            boolean shouldExit = false;

            Scanner keyboardScanner = new Scanner(System.in);
            int userInput;

            System.out.println("\n\t\t\t\t*** N-Queens Problem Visualizer ***\n");
            while (true) {
                System.out.println("> To start, enter an integer number N > 0, with N != 2 and N != 3: ");
                try {
                    userInput = Integer.parseInt(keyboardScanner.nextLine());
                    if (userInput > 0 && userInput != 2 && userInput != 3) {
                        break;
                    } else {
                        System.out.println("> Wrong number! Please enter an integer within the specified range and try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("> Wrong Input! Please enter a number and try again.");
                }
            }

            System.out.println("> Began calculating all possible solutions of the N-Queens problem for the given N number...");
            Solver solver = new Solver(userInput);
            solver.startSolving();
            solver.printSolutionGrid();

            while (true) {
                System.out.println("> Enter 1 to try again with another number or 2 to exit: ");
                try {
                    userInput = Integer.parseInt(keyboardScanner.nextLine());
                    if (userInput == 1) {
                        break;
                    } else if (userInput == 2) {
                        shouldExit = true;
                        break;
                    } else {
                        System.out.println("> Wrong number! Please enter an integer within the specified range and try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("> Wrong Input! Please enter a number and try again.");
                }
            }

            if (shouldExit) {
                break;
            }
        }
    }
}