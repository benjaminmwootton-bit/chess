package chess;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessPiece.PieceType.BISHOP;
import static chess.ChessPiece.PieceType.KNIGHT;
import static chess.ChessPiece.PieceType.ROOK;
import static chess.ChessPiece.PieceType.PAWN;
import static chess.ChessPiece.PieceType.QUEEN;
import static chess.ChessPiece.PieceType.KING;

import static java.awt.Color.black;

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
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)
    {
        board[position.getRow()][position.getColumn()] = piece;
        //throw new RuntimeException("Not implemented");
    }
    //overload
    public void addPiece(int row, int column, ChessPiece piece)
    {
        board[row][column] = piece;
        //throw new RuntimeException("Not implemented");
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
        /**
        *        this.board = {{new ChessPiece(BLACK, ROOK), new ChessPiece(BLACK, KNIGHT), new ChessPiece(BLACK, BISHOP), new ChessPiece(BLACK, QUEEN), new ChessPiece(BLACK, KING), new ChessPiece(BLACK, BISHOP), new ChessPiece(BLACK, KNIGHT), new ChessPiece(BLACK, ROOK)},
        *        {new ChessPiece(BLACK, PAWN), new ChessPiece(BLACK, PAWN), new ChessPiece(BLACK, PAWN), new ChessPiece(BLACK, PAWN), new ChessPiece(BLACK, PAWN), new ChessPiece(BLACK, PAWN), new ChessPiece(BLACK, PAWN), new ChessPiece(BLACK, PAWN)},
        *        {null, null, null, null, null, null, null, null},
        *        {null, null, null, null, null, null, null, null},
        *        {null, null, null, null, null, null, null, null},
        *        {null, null, null, null, null, null, null, null},
        *        {new ChessPiece(WHITE, PAWN), new ChessPiece(WHITE, PAWN), new ChessPiece(WHITE, PAWN), new ChessPiece(WHITE, PAWN), new ChessPiece(WHITE, PAWN), new ChessPiece(WHITE, PAWN), new ChessPiece(WHITE, PAWN), new ChessPiece(WHITE, PAWN)},
        *        {new ChessPiece(WHITE, ROOK), new ChessPiece(WHITE, KNIGHT), new ChessPiece(WHITE, BISHOP), new ChessPiece(WHITE, QUEEN), new ChessPiece(WHITE, KING), new ChessPiece(WHITE, BISHOP), new ChessPiece(WHITE, KNIGHT), new ChessPiece(WHITE, ROOK)}
        *};
         */
        addPiece(7, 0, new ChessPiece(BLACK, ROOK));
        addPiece(7, 1, new ChessPiece(BLACK, KNIGHT));
        addPiece(7, 2, new ChessPiece(BLACK, BISHOP));
        addPiece(7, 3, new ChessPiece(BLACK, QUEEN));
        addPiece(7, 4, new ChessPiece(BLACK, KING));
        addPiece(7, 5, new ChessPiece(BLACK, BISHOP));
        addPiece(7, 6, new ChessPiece(BLACK, KNIGHT));
        addPiece(7, 7, new ChessPiece(BLACK, ROOK));

        addPiece(6, 0, new ChessPiece(BLACK, PAWN));
        addPiece(6, 1, new ChessPiece(BLACK, PAWN));
        addPiece(6, 2, new ChessPiece(BLACK, PAWN));
        addPiece(6, 3, new ChessPiece(BLACK, PAWN));
        addPiece(6, 4, new ChessPiece(BLACK, PAWN));
        addPiece(6, 5, new ChessPiece(BLACK, PAWN));
        addPiece(6, 6, new ChessPiece(BLACK, PAWN));
        addPiece(6, 7, new ChessPiece(BLACK, PAWN));

        addPiece(1, 0, new ChessPiece(WHITE, PAWN));
        addPiece(1, 1, new ChessPiece(WHITE, PAWN));
        addPiece(1, 2, new ChessPiece(WHITE, PAWN));
        addPiece(1, 3, new ChessPiece(WHITE, PAWN));
        addPiece(1, 4, new ChessPiece(WHITE, PAWN));
        addPiece(1, 5, new ChessPiece(WHITE, PAWN));
        addPiece(1, 6, new ChessPiece(WHITE, PAWN));
        addPiece(1, 7, new ChessPiece(WHITE, PAWN));

        addPiece(0, 0, new ChessPiece(WHITE, ROOK));
        addPiece(0, 1, new ChessPiece(WHITE, KNIGHT));
        addPiece(0, 2, new ChessPiece(WHITE, BISHOP));
        addPiece(0, 3, new ChessPiece(WHITE, QUEEN));
        addPiece(0, 4, new ChessPiece(WHITE, KING));
        addPiece(0, 5, new ChessPiece(WHITE, BISHOP));
        addPiece(0, 6, new ChessPiece(WHITE, KNIGHT));
        addPiece(0, 7, new ChessPiece(WHITE, ROOK));
        //throw new RuntimeException("Not implemented");
    }
}
