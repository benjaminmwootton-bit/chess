package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard
{
    public ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard()
    {
        this.board =[
        [ChessPiece(black, ROOK),ChessPiece(black, KNIGHT),ChessPiece(black, BISHOP),ChessPiece(black, QUEEN),ChessPiece(black, KING),ChessPiece(black, BISHOP),ChessPiece(black, KNIGHT) ,ChessPiece(black, ROOK)],
        [ChessPiece(black, PAWN),ChessPiece(black, PAWN),ChessPiece(black, PAWN),ChessPiece(black, PAWN),ChessPiece(black, PAWN),ChessPiece(black, PAWN),ChessPiece(black, PAWN),ChessPiece(black, PAWN),]
        [null,null,null,null,null,null,null,null],
        [null,null,null,null,null,null,null,null],
        [null,null,null,null,null,null,null,null],
        [null,null,null,null,null,null,null,null],
        [ChessPiece(white, PAWN),ChessPiece(white, PAWN),ChessPiece(white, PAWN),ChessPiece(white, PAWN),ChessPiece(white, PAWN),ChessPiece(white, PAWN),ChessPiece(white, PAWN),ChessPiece(white, PAWN),],
        [ChessPiece(white, ROOK),ChessPiece(white, KNIGHT),ChessPiece(white, BISHOP),ChessPiece(white, QUEEN),ChessPiece(white, KING),ChessPiece(white, BISHOP),ChessPiece(white, KNIGHT) ,ChessPiece(white, ROOK)]
        ];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position)
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard()
    {
        throw new RuntimeException("Not implemented");
    }
}
