package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPosition(int row, int column)
{
    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    @Override
    public int row() {return row;}
    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    @Override
    public int column() {return column;}

    public String toString() {
        return "[" + row + "," + column + "]";
    }
}