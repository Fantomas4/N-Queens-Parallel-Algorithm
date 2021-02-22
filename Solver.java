import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Solver {
    int gridSize; // The size of the grid to be used (number "n" of the n-queens problem).
    ArrayList<Integer[]> results = new ArrayList<>(); // Used to store the solutions found for the given n-queens problem.
    int threadsNumber; // The number of threads that should be used in finding a solution to the n-queens problem.
    ExecutorService taskExecutor; // Executor used to manage the active threads.

    public Solver(int gridSize, int threadsNumber) {
        this.gridSize = gridSize;
        this.threadsNumber = threadsNumber;
        this.taskExecutor = Executors.newFixedThreadPool(threadsNumber);
    }

    public void startSolving() {
        for (int i = 0; i < gridSize; i++) {
            Integer[] cols = new Integer[this.gridSize];
            cols[0] = i;
            taskExecutor.execute(new QueenTask(1, cols));
        }
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Used by the task executor to assign solution tasks to individual threads.
    class QueenTask implements Runnable {
        int col;
        Integer[] cols;

        public QueenTask(int col, Integer[] cols) {
            this.col = col;
            this.cols = cols;
        }

        public void run() {
            solveQueens(this.col, this.cols);
        }
    }

    // ATTENTION! No N-Queens placement solution exists for N=2 or N=3
    private void solveQueens(int col, Integer[] cols) {
        if (col == this.gridSize) {
            // Found a complete n-queen solution
            synchronized(this) {
                this.results.add(cols.clone());
            }
        } else {
            // Continue looking for a solution recursively
            for (int row = 0; row < this.gridSize; row++) {
                if (checkValidity(cols, col, row)) {
                    cols[col] = row; // Place queen on the grid
                    solveQueens(col + 1, cols);
                }
            }
        }
    }

    // Check if (col1, row1) is a valid spot for a queen by checking if there is
    // a queen in the same row or diagonal. We do not need to check it for queens
    // in the same column because the calling solveQueens only attempts to place one
    // queen at a time. We know this column is empty.
    private boolean checkValidity(Integer[] cols, int col1, int row1) {
        for (int col2 = 0; col2 < col1; col2++) {
            // Check if (col2, row2) invalidates (col1, row1) as a queen
            // placement spot.
            int row2 = cols[col2];

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

    // Returns the amount of digits in a number.
    private char[] getNumberDigitChars(int number) {
        return String.valueOf(number).toCharArray();
    }

    // Prints all the solutions that have been found for the given N-Queens problem scenario.
    public void printSolutionGrid() {
        System.out.printf("%n> There are %d solutions to the %d-Queens problem:%n%n", this.results.size(), this.gridSize);

        // Calculate the length of the given grid size number
        int gridSizeNumLen = String.valueOf(this.gridSize - 1).length();

        // The size for the grid used to print the results,
        // taking the additional space needed for border characters
        // into account.
        int printGridSize = this.gridSize + gridSizeNumLen + 2;

        int solCounter = 0; // Counter used to keep track of the solution currently being printed

        // Print a grid containing the queens positions
        // for each n-queens solution found
        for (Integer[] solution : this.results) {
            solCounter++;
            System.out.printf(" Solution %d%n", solCounter);
            System.out.print(" Queens placed at:");
            // Print the queen placement coordinates of the current solution
            for (int q = 0; q < solution.length; q++) {
                System.out.printf(" [%d, %d]", q, solution[q]);
                if (q != solution.length - 1) {
                    System.out.print(",");
                }
            }
            System.out.print("\n\n");

            String[][] grid = new String[printGridSize][printGridSize];

            // Initialize all grid positions with " "
            for (int i = 0; i < printGridSize; i++) {
                for (int j = 0; j < printGridSize; j++) {
                    grid[i][j] = " ";
                }
            }

            // Set reference numbers at the left border of the grid
            for (int i = 1; i <= this.gridSize; i++) {
                int indexNumber = this.gridSize - i;
                char[] digitChars = getNumberDigitChars(indexNumber);
                int k = digitChars.length - 1; // digitChars index counter

                for (int j = gridSizeNumLen - 1; j >= 0; j--) {
                    if (k >= 0) {
                        grid[i][j] = Character.toString(digitChars[k]);
                        k--;
                    } else {
                        grid[i][j] = " ";
                    }
                }
            }

            // Set reference numbers at the bottom border of the grid
            for (int j = gridSizeNumLen + 1; j < printGridSize - 1; j++) {
                int indexNumber = j - gridSizeNumLen - 1;
                char[] digitChars = getNumberDigitChars(indexNumber);
                int k = 0; // digitChars index counter

                for (int i = printGridSize - gridSizeNumLen; i < printGridSize ; i++) {
                    if (k < digitChars.length) {
                        grid[i][j] = String.format(" %c ", digitChars[k]);
                        k++;
                    } else {
                        grid[i][j] = "   ";
                    }
                }
            }

            // Set the + symbol at the grid corners
            grid[0][gridSizeNumLen] = "+";                                     // top left corner
            grid[0][printGridSize - 1] = "+";                                  // top right corner
            grid[printGridSize - gridSizeNumLen - 1][gridSizeNumLen] = "+";    // bottom left corner
            grid[printGridSize - gridSizeNumLen - 1][printGridSize - 1] = "+"; // bottom right corner

            // Set the - symbol at the top horizontal grid border
            for (int j = gridSizeNumLen + 1; j < printGridSize - 1; j++) {
                grid[0][j] = " - ";
            }

            // Set the - symbol at the bottom horizontal grid border
            for (int j = gridSizeNumLen + 1; j < printGridSize - 1; j++) {
                grid[printGridSize - gridSizeNumLen - 1][j] = " - ";
            }

            // Set the | symbol at the left vertical grid border
            for (int i = 1; i < printGridSize - gridSizeNumLen - 1; i++) {
                grid[i][gridSizeNumLen] = "|";
            }

            // Set the | symbol at the right vertical grid border
            for (int i = 1; i < printGridSize - gridSizeNumLen - 1; i++) {
                grid[i][printGridSize - 1] = "|";
            }

            // Initialize all possible queen positions with .
            for (int i = 1; i <= this.gridSize; i++) {
                for (int j = gridSizeNumLen + 1; j < printGridSize - 1; j++) {
                    grid[i][j] = " . ";
                }
            }

            // Add the queens positions to the grid
            for (int q = 0; q < solution.length; q++) {
                grid[this.gridSize - q][gridSizeNumLen + 1 + solution[q]] = " X ";
            }

            // Print the generated grid
            for (int i = 0; i < printGridSize; i++) {
                for (int j = 0; j < printGridSize; j++) {
                    System.out.print(grid[i][j]);
                }
                System.out.println();
            }

            System.out.print("\n\n\n");
        }
    }

    public static void main(String[] args) {
        while (true) {
            boolean shouldExit = false;
            Scanner keyboardScanner = new Scanner(System.in);
            int requestedThreads;
            int requestedN;
            int userInput;

            System.out.println("\n\t\t\t\t*** N-Queens Problem Visualizer ***\n");
            while (true) {
                System.out.println("> Enter the number of threads that should be used: ");
                try {
                    userInput = Integer.parseInt(keyboardScanner.nextLine());
                    if (userInput > 0) {
                        requestedThreads = userInput;
                        break;
                    } else {
                        System.out.println("> Wrong number! Please enter an integer greater than 0 and try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("> Wrong Input! Please enter an integer number and try again.");
                }
            }
            while (true) {
                System.out.println("> To start, enter an integer number N > 0, with N != 2 and N != 3: ");
                try {
                    userInput = Integer.parseInt(keyboardScanner.nextLine());
                    if (userInput > 0 && userInput != 2 && userInput != 3) {
                        requestedN = userInput;
                        break;
                    } else {
                        System.out.println("> Wrong number! Please enter an integer within the specified range and try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("> Wrong Input! Please enter an integer number and try again.");
                }
            }

            System.out.println("> Began calculating all possible solutions of the N-Queens problem for the given N number...");
            Solver solver = new Solver(requestedN, requestedThreads);
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