import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Utils {

    // Function to get files under /inputs
    public static File[] getFileNames(String dirPath) {
        try {
            File inputDir = new File(dirPath);
            File[] inputFiles = inputDir.listFiles();
            int fileNum = 0;
            if (inputFiles != null) {
                System.out.println("Current files in our input directory:");
                for (File file : inputFiles) {
                    fileNum += 1;
                    System.out.println("   [" + fileNum + "] " + file.getName()); // printing out the files' names
                }
            } else {
                System.out.println("There are no input files in our directory!");
            }
            return inputFiles;
        } catch (Exception e) {
            System.err.println("Could not find the selected file!");
            return null;
        }
    }

    // Function to ask for input file selection
    public static File selectFile() {

        // Getting the list of input files under src/inputs
        File[] inputFiles = getFileNames("src/inputs/");

        // Getting the selection from user
        int selectedNumber;
        Scanner s1 = new Scanner(System.in);
        System.out.println();

        // a loop to get user input for file selection
        while (true) {
            try {
                System.out.print("Please select an option from " + "[1] to [" + inputFiles.length + "]: ");
                selectedNumber = s1.nextInt();
                if ((selectedNumber > 0) && (selectedNumber <= inputFiles.length)) {
                    System.out.println();
                    System.out.println("You have selected the following file: " + inputFiles[selectedNumber - 1].getName());
                    break;
                }
            } catch (InputMismatchException exception) {
                System.out.println("!! Integers only, please !!");
                s1.next();
            }
        }

        return new File("src/inputs/" + inputFiles[selectedNumber - 1].getName());
    }

    // Function to select process input text then return a list of 9x9 puzzles
    public static ArrayList<char[][]> processInputFile() {
        File inputFile = selectFile();
        int numberOfPuzzles;
        ArrayList<char[][]> puzzleList = new ArrayList<>();

        try {
            Scanner s = new Scanner(inputFile);
            numberOfPuzzles = s.nextInt();
            puzzleList = new ArrayList<>();

            while (s.hasNext()) {
                char[][] puzzle2DArr = new char[3][3];
                for (int j = 0; j < 3; j++) {
                    for (int a = 0; a < 3; a++) {
                        puzzle2DArr[j][a] = s.next().charAt(0);
                    }
                }
                puzzleList.add(puzzle2DArr);
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return puzzleList;
    }

    // make a new array not dependent/reference the original array.
    public static char[][] arrayDeepCopy(char[][] arrB) {
        char[][] arrA = new char [arrB.length][arrB.length];
        for(int i=0; i<arrB.length; i++) {
            for (int j=0; j<arrB.length; j++) {
                arrA[i][j] = arrB[i][j];
            }
        }
        return arrA;
    }

    public static void printPuzzle(char[][] puzzle) {
        for(int i=0; i<puzzle.length; i++) {
            System.out.println();
            for(int j=0; j<puzzle.length; j++) {
                System.out.print(" " + puzzle[i][j] + " ");
            }
        }
        System.out.println();
    }

    // Apply moves and generate successors to the current State
    public static ArrayList<Node> genSuccessors(Node node) {
        node.searchEmptyTile(node.getCurState());
        int rowPos = node.getEmptyTileRowPos();
        int colPos = node.getEmptyTileColPos();
        ArrayList<Node> successors = new ArrayList<>();

        if ((rowPos * Node.EIGHT_PUZZLE_DIMENSION + colPos) % Node.EIGHT_PUZZLE_DIMENSION != (Node.EIGHT_PUZZLE_DIMENSION-1)) {
            // move empty right
            char[][] successorState = arrayDeepCopy(node.getCurState());
            char tile = successorState[rowPos][colPos];
            successorState [rowPos][colPos] = successorState [rowPos][colPos + 1];
            successorState [rowPos][colPos + 1] = tile;

            Node aSuccessor = new Node(node,successorState,"RIGHT");
            aSuccessor.calHCost();
            successors.add(aSuccessor);
        }

        if ((rowPos * Node.EIGHT_PUZZLE_DIMENSION + colPos) / Node.EIGHT_PUZZLE_DIMENSION != (Node.EIGHT_PUZZLE_DIMENSION-1)) {
            // move empty down
            char[][] successorState = arrayDeepCopy(node.getCurState());
            char tile = successorState[rowPos][colPos];
            successorState [rowPos][colPos] = successorState[rowPos+1][colPos];
            successorState [rowPos+1][colPos] = tile;

            Node child = new Node(node, successorState, "DOWN");
            child.calHCost();
            successors.add(child);
        }

        if ((rowPos * Node.EIGHT_PUZZLE_DIMENSION + colPos) % Node.EIGHT_PUZZLE_DIMENSION != 0) {
            // move empty left
            char[][] successorState = arrayDeepCopy(node.getCurState());
            char tile = successorState[rowPos][colPos];
            successorState [rowPos][colPos] = successorState [rowPos][colPos-1];
            successorState [rowPos][colPos-1] = tile;

            Node child = new Node(node, successorState,"LEFT");
            child.calHCost();
            successors.add(child);
        }

        if ((rowPos * Node.EIGHT_PUZZLE_DIMENSION + colPos) / Node.EIGHT_PUZZLE_DIMENSION != 0) {
            // move empty up
            char[][] successorState = arrayDeepCopy(node.getCurState());
            char tile = successorState[rowPos][colPos];
            successorState[rowPos][colPos] = successorState [rowPos-1][colPos];
            successorState [rowPos-1][colPos] = tile;

            Node child = new Node(node, successorState, "UP");
            child.calHCost();
            successors.add(child);
        }
        return successors;
    }

    // A* search return the solution Node. If the puzzle can't be solve, then return NULL
    public static Node AStarSearch(char[][] initial) {
        Node root = new Node(initial);
        PriorityQueue<Node> frontier = new PriorityQueue<>();  // ordered by Manhattan heuristic function
        frontier.add(root);

        HashMap <String, Node> exploredSet = new HashMap<>();  // to store expanded nodes

        Node solution = null;

        while (!frontier.isEmpty()) {
            Node currentNode = frontier.remove();
            currentNode.calHCost();

            if (currentNode.isGoal()) {
                solution = currentNode;
                break;
            } else {
                // make sure current node isn't in our explored node set
                if (!exploredSet.containsKey(Arrays.deepToString(currentNode.getCurState()))) {
                    exploredSet.put(Arrays.deepToString(currentNode.getCurState()), currentNode);
                    frontier.addAll(genSuccessors(currentNode));
                }
            }
        }
        return solution;
    }

    // I'm keeping the move of Empty Tile so this function
    // is to get the move of the tile being moved into the empty tile
    private static String slidingAction(String emptyTileMove) {
        String slidingAction = "";
        switch (emptyTileMove) {
            case "RIGHT":
                slidingAction = "LEFT";
                break;
            case "LEFT":
                slidingAction = "RIGHT";
                break;
            case "UP":
                slidingAction = "DOWN";
                break;
            case "DOWN":
                slidingAction = "UP";
                break;
            default:
                System.out.println("No Match");
        }

        return slidingAction;
    }

    // print solution from starting to finish
    public static void printSolPath(Node node) {
        Stack<Node> solStack = new Stack<>();
        solStack.add(node);
        while (node.getParent() != null) {
            node = node.getParent();
            solStack.add(node);
        }

        solStack.pop(); // This is the inital puzzle state which was already printed out in the EightPuzzle program.

        while (!solStack.isEmpty()) {
            Node a = solStack.pop();
            System.out.println("\n ==> Slide a tile " + slidingAction(a.getAction()) + " to E <==");
            printPuzzle(a.getCurState());
        }

        System.out.println("\n=====> PUZZLE SOLVED!!!");
    }

    /*
    // https://www.geeksforgeeks.org/check-instance-8-puzzle-solvable/
    private static int getInvCount(char[][] a)
    {
        char[] arr = new char[9];
        for (int i=0; i<3;i++) {
            for (int j=0; j<3; j++) {
                arr[i*3+j] = a[i][j];
            }
        }

        int inv_count = 0;
        for (int i = 0; i < 9 - 1; i++)
            for (int j = i+1; j < 9; j++)
                if (arr[j] != 'E' && arr[i] != 'E' &&  arr[i] > arr[j])
                    inv_count++;
        return inv_count;
    }

    public static boolean isSolvable(char[][] a) {
        // Count inversions in given 8 puzzle
        int invCount = getInvCount(a);

        // return true if inversion count is even.
        return (invCount%2 == 0);

    }
    */
}