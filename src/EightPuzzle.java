import java.util.ArrayList;
public class EightPuzzle {

    static char [][] goalState  =     {{'1','2','3'},{'4','5','6'},{'7','8','E'}};

    public static void main(String[] args) {
        System.out.println("\n************ 8-PUZZLE WITH A* MANHATTAN HEURISTIC ************\n");

        // ask user for input file, process it and return a list of 2D char puzzle arrays
        ArrayList<char[][]> puzzleList = Utils.processInputFile();

        System.out.println("Number of puzzles: " + puzzleList.size());

        char[][] puzzle;

        for (int i=0; i<puzzleList.size(); i++) {
            System.out.println("\n * Puzzle #" + (i + 1) + ":");

            puzzle = puzzleList.get(i);
            Utils.printPuzzle(puzzle); // print current puzzle for display

            /* Testing to make sure I'm getting correct answers
            if (Utils.isSolvable(puzzle)) {
                System.out.println("CAN BE SOLVED");
            } else {
                System.out.println("CAN NOT BE SOLVED");
            }
            */

            Node solution = Utils.AStarSearch(puzzle);
            if (solution == null) {
                System.out.println("\n==> This puzzle is not solvable!");
            } else {
                Utils.printSolPath(solution);
            }
        }
    }
}