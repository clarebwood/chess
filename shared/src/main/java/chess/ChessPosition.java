package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    int coolRow;
    int coolCol;

    public ChessPosition(int row, int col) {
        coolRow = row - 1;
        coolCol = col - 1;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return coolRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return coolCol;
    }

    public ChessPosition move(int rowOffset, int colOffset) {
        return new ChessPosition(coolRow + rowOffset + 1, coolCol + colOffset + 1);
    }
}
