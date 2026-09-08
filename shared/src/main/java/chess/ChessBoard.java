package chess;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessPiece.PieceType.BISHOP;
import static chess.ChessPiece.PieceType.KNIGHT;
import static chess.ChessPiece.PieceType.ROOK;
import static chess.ChessPiece.PieceType.PAWN;
import static chess.ChessPiece.PieceType.QUEEN;
import static chess.ChessPiece.PieceType.KING;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard
{
    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)
    {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }
    //overload. takes row, column, and piece
    public void addPiece(int row, int column, ChessPiece piece)
    {
        board[row-1][column-1] = piece;
    }
    //set piece to null
    public void removePiece(int row, int column)
    {
        board[row-1][column-1] = null;
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
        return board[position.getRow()-1][position.getColumn()-1];
    }
    public ChessPiece getPiece(int row, int column)
    {
        return board[row-1][column-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard()
    {
        for(int i = 1; i < 9;i++)
        {
            for(int x = 1; x < 9; x++)
            {
                removePiece(x,i);
            }
        }
        addPiece(8, 1, new ChessPiece(BLACK, ROOK));
        addPiece(8, 2, new ChessPiece(BLACK, KNIGHT));
        addPiece(8, 3, new ChessPiece(BLACK, BISHOP));
        addPiece(8, 4, new ChessPiece(BLACK, QUEEN));
        addPiece(8, 5, new ChessPiece(BLACK, KING));
        addPiece(8, 6, new ChessPiece(BLACK, BISHOP));
        addPiece(8, 7, new ChessPiece(BLACK, KNIGHT));
        addPiece(8, 8, new ChessPiece(BLACK, ROOK));

        addPiece(7, 1, new ChessPiece(BLACK, PAWN));
        addPiece(7, 2, new ChessPiece(BLACK, PAWN));
        addPiece(7, 3, new ChessPiece(BLACK, PAWN));
        addPiece(7, 4, new ChessPiece(BLACK, PAWN));
        addPiece(7, 5, new ChessPiece(BLACK, PAWN));
        addPiece(7, 6, new ChessPiece(BLACK, PAWN));
        addPiece(7, 7, new ChessPiece(BLACK, PAWN));
        addPiece(7, 8, new ChessPiece(BLACK, PAWN));

        addPiece(2, 1, new ChessPiece(WHITE, PAWN));
        addPiece(2, 2, new ChessPiece(WHITE, PAWN));
        addPiece(2, 3, new ChessPiece(WHITE, PAWN));
        addPiece(2, 4, new ChessPiece(WHITE, PAWN));
        addPiece(2, 5, new ChessPiece(WHITE, PAWN));
        addPiece(2, 6, new ChessPiece(WHITE, PAWN));
        addPiece(2, 7, new ChessPiece(WHITE, PAWN));
        addPiece(2, 8, new ChessPiece(WHITE, PAWN));

        addPiece(1, 1, new ChessPiece(WHITE, ROOK));
        addPiece(1, 2, new ChessPiece(WHITE, KNIGHT));
        addPiece(1, 3, new ChessPiece(WHITE, BISHOP));
        addPiece(1, 4, new ChessPiece(WHITE, QUEEN));
        addPiece(1, 5, new ChessPiece(WHITE, KING));
        addPiece(1, 6, new ChessPiece(WHITE, BISHOP));
        addPiece(1, 7, new ChessPiece(WHITE, KNIGHT));
        addPiece(1, 8, new ChessPiece(WHITE, ROOK));
    }
}