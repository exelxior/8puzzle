import java.util.Arrays;

public class Node implements Comparable<Node> {

    static int EIGHT_PUZZLE_DIMENSION = 3; // cause we're doing 8-puzzle only 9x9 board
    private char[][] curState;   // puzzle
    private int hCost; // to store heuristic cost
    private Node parent; // parent node
    private String action;  // move action of the empty tile
    private int emptyTileRowPos, emptyTileColPos;

    Node(char[][] parentState) {
        this.curState = parentState;
    }

    Node(Node parent, char[][] parentState, String action) {
        this.parent = parent;
        this.curState = parentState;
        this.action = action;
    }

    public char[][] getCurState(){ return this.curState; }

    // Function to calculate heuristic cost and save it to this node
    public void calHCost() { this.hCost = calManDist(EightPuzzle.goalState); }

    public Node getParent() { return this.parent; }

    public String getAction() { return this.action; }

    public int getEmptyTileRowPos() { return emptyTileRowPos; }

    public int getEmptyTileColPos() { return emptyTileColPos; }

    // Function to search and setup empty tile position
    public void searchEmptyTile(char[][] state) {
        boolean found = false;
        for (int i=0 ; i<EIGHT_PUZZLE_DIMENSION && !found; i++) {
            for (int j=0; j<EIGHT_PUZZLE_DIMENSION; j++) {
                if (state[i][j] == 'E') {  // change to 'E'
                    this.emptyTileRowPos = i;
                    this.emptyTileColPos = j;
                    found = true; // Stop search completely
                    break ;
                }
            }
        }
    }

    // Function to calculate Manhattan Distance
    public int calManDist(char[][] goalState) {
        int manDist = 0;
        int currentTile;

        for(int i = 0; i<this.curState.length; i++) {
            for(int j = 0; j<this.curState.length; j++) {
                if (curState[i][j] == goalState[i][j] || curState[i][j] == 'E') {
                    break;
                } else {
                    currentTile = curState[i][j];
                    manDist += calManDist1Tile(currentTile, i, j, goalState); // sum of vertical & horizontal distances
                }
            }
        }
        return manDist;
    }

    // Function to calculate manhattan Distance for a tile in the puzzle
    public int calManDist1Tile(int currentTile, int row, int  col, char[][] goalState ) {
        int manDistCurrentTile = 0;
        boolean foundTile = false; // To break out of loops faster

        // Looping through goalState to find a tile with the same value
        for(int i=0; i<goalState.length && !foundTile; i++) {
            for(int j=0; j<goalState.length; j++) {
                if (goalState[i][j] == currentTile) { //found
                    manDistCurrentTile = Math.abs(row-i) + Math.abs(col-j);  // vertical & horizontal distance
                    foundTile = true;
                    break;
                }
            }
        }
        return manDistCurrentTile;
    }

    // so our PQ can compare the nodes by our heuristic function
    @Override
    public int compareTo(Node o) {
        if(this.hCost < o.hCost) {
            return -1;
        } else if(this.hCost > o.hCost) {
            return 1;
        } else return 0;
    }

    // Function to check if at goal curState
    public boolean isGoal() {
        return Arrays.deepEquals(Utils.arrayDeepCopy(this.curState), EightPuzzle.goalState);
    }
}