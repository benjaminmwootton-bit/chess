package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

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
        board[position.row()-1][position.column()-1] = piece;
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
    public void removePiece(ChessPosition pos)
    {
        int row = pos.row(); int column = pos.column();
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
        return board[position.row()-1][position.column()-1];
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
        board = new ChessPiece[8][8];

        addPiece(8, 1, new ChessPiece(BLACK, ROOK));
        addPiece(8, 2, new ChessPiece(BLACK, KNIGHT));
        addPiece(8, 3, new ChessPiece(BLACK, BISHOP));
        addPiece(8, 4, new ChessPiece(BLACK, QUEEN));
        addPiece(8, 5, new ChessPiece(BLACK, KING));
        addPiece(8, 6, new ChessPiece(BLACK, BISHOP));
        addPiece(8, 7, new ChessPiece(BLACK, KNIGHT));
        addPiece(8, 8, new ChessPiece(BLACK, ROOK));

        for (int column = 1; column <= 8; column++) {
            addPiece(7, column, new ChessPiece(BLACK, PAWN));
            addPiece(2, column, new ChessPiece(WHITE, PAWN));
        }

        addPiece(1, 1, new ChessPiece(WHITE, ROOK));
        addPiece(1, 2, new ChessPiece(WHITE, KNIGHT));
        addPiece(1, 3, new ChessPiece(WHITE, BISHOP));
        addPiece(1, 4, new ChessPiece(WHITE, QUEEN));
        addPiece(1, 5, new ChessPiece(WHITE, KING));
        addPiece(1, 6, new ChessPiece(WHITE, BISHOP));
        addPiece(1, 7, new ChessPiece(WHITE, KNIGHT));
        addPiece(1, 8, new ChessPiece(WHITE, ROOK));
    }
    //prints out board
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append("\n\u001b[31;100;1m    a  b  c  d  e  f  g  h    \u001b[0m\n");
        for (int i = 7; i >= 0; i--) {
            str.append("\u001b[31;100;1m ").append(i + 1).append(" \u001b[0m");
            for (int x = 0; x < 8; x++) {
                str.append(toStringHelper(i, x));}
            str.append("\u001b[31;100;1m ").append(i + 1).append(" \u001b[0m\n");
        }
        str.append("\u001b[31;100;1m    a  b  c  d  e  f  g  h    \u001b[0m");
        return str.toString();
    }

    public String toStringBlack() {
        StringBuilder str = new StringBuilder();
        str.append("\n\u001b[31;100;1m    h  g  f  e  d  c  b  a    \u001b[0m\n");
        for (int i = 0; i < 8; i++) {
            str.append("\u001b[31;100;1m ").append(i + 1).append(" ");
            for (int x = 7; x >= 0; x--) {
                str.append(toStringHelper(i, x));}
            str.append("\u001b[31;100;1m ").append(i + 1).append(" \u001b[0m\n");
        }
        str.append("\u001b[31;100;1m    h  g  f  e  d  c  b  a    \u001b[0m");
        return str.toString();
    }

    public String toStringHelper(int i, int x)
    {
        StringBuilder str = new StringBuilder();
        if ((i + x) % 2 == 0) {
            str.append("\u001b[104m");}
        else {
            str.append("\u001b[44m");}
        if (board[i][x] != null) {
            if (board[i][x].getTeamColor() == WHITE) {
                str.append("\u001b[31m");}
            else {
                str.append("\u001b[30m");}
            str.append(" ");
            str.append(board[i][x]);
        }
        else {
            str.append("  ");}
        str.append(" ");
        str.append("\u001b[0m");
        return str.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode(board) * 17;
    }
}