package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessPiece.PieceType.BISHOP;
import static chess.ChessPiece.PieceType.KNIGHT;
import static chess.ChessPiece.PieceType.ROOK;
import static chess.ChessPiece.PieceType.PAWN;
import static chess.ChessPiece.PieceType.QUEEN;
import static chess.ChessPiece.PieceType.KING;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece
{
    private ChessGame.TeamColor pieceColor;
    private PieceType type;
    private boolean hasMoved;
    private int row;
    private int column;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = false;
    }

    public String toString()
    {
        if (this.pieceColor == BLACK)
        {
            if (this.type == ROOK)
                return "♜";
            else if(this.type == KNIGHT)
                return "♞";
            else if (this.type == BISHOP)
                return "♝";
            else if (this.type == QUEEN)
                return "♛";
            else if (this.type == KING)
                return "♚";
            else if (this.type == PAWN)
                return "♟";
            else
                return null;
        }
        else if (this.pieceColor == WHITE)
        {
            if (this.type == ROOK)
                return "♖";
            else if(this.type == KNIGHT)
                return "♘";
            else if (this.type == BISHOP)
                return "♗";
            else if (this.type == QUEEN)
                return "♕";
            else if (this.type == KING)
                return "♔";
            else if (this.type == PAWN)
                return "♙";
            else
                return null;
        }
        else
            return null;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType
    {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor()
    {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> moves = new ArrayList<>();
        if(board.getPiece(myPosition).type == PAWN)
        {
            moves = pawnMoves(board, myPosition);
        }
        else if(board.getPiece(myPosition).type == ROOK)
        {
            moves = rookMoves(board, myPosition);
        }
        else if(board.getPiece(myPosition).type == BISHOP)
        {
            moves = bishopMoves(board, myPosition);
        }
        else if(board.getPiece(myPosition).type == KNIGHT)
        {
            moves = knightMoves(board, myPosition);
        }
        else if(board.getPiece(myPosition).type == QUEEN)
        {
            moves = queenMoves(board, myPosition);
        }
        else if(board.getPiece(myPosition).type == KING)
        {
            moves = kingMoves(board, myPosition);
        }
        return moves;
    }

    //Move rules for different pieces
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> rookMoves = new ArrayList<>();
        int rowPos = myPosition.getRow(); int colPos = myPosition.getColumn();
        //left
        rowPos--;
        while(rowPos >= 1 && board.getPiece(rowPos, colPos) == null)
        {
            rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos, colPos), null));
            rowPos--;
        }
        //capture
        if(rowPos >= 1 && board.getPiece(rowPos,colPos).pieceColor != board.getPiece(myPosition).pieceColor)
            rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos, colPos), null));
        rowPos = myPosition.getRow();
        //right
        rowPos++;
        while(rowPos <= 8 && board.getPiece(rowPos, colPos) == null)
        {
            rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos, colPos), null));
            rowPos++;
        }
        if(rowPos <= 8 && board.getPiece(rowPos,colPos).pieceColor != board.getPiece(myPosition).pieceColor)
            rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos, colPos), null));
        rowPos = myPosition.getRow();
        //down
        colPos--;
        while(colPos >= 1 && board.getPiece(rowPos, colPos) == null)
        {
            rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos, colPos), null));
            colPos--;
        }
        //capture
        if(colPos >= 1 && board.getPiece(rowPos,colPos).pieceColor != board.getPiece(myPosition).pieceColor)
            rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos, colPos), null));
        colPos = myPosition.getColumn();
        //up
        colPos++;
        while(colPos <= 8 && board.getPiece(rowPos, colPos) == null)
        {
            rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos, colPos), null));
            colPos++;
        }
        if(colPos <= 8 && board.getPiece(rowPos,colPos).pieceColor != board.getPiece(myPosition).pieceColor)
            rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos, colPos), null));
        return rookMoves;
    }
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition)
    {
        throw new RuntimeException("Not implemented");
    }
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition)
    {
        throw new RuntimeException("Not implemented");
    }
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition)
    {
        throw new RuntimeException("Not implemented");
    }
    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition)
    {
        throw new RuntimeException("Not implemented");
    }
    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition)
    {
        /**
         row = this.getRow();
         column = this.getColumn();
         //I love pawns. They move so simply...
         if(this.getPieceType() == PAWN)
         {
         if (this.getTeamColor() == WHITE)
         {
         if (this.hasMoved == false)
         {
         if(this.getRow() )
         }
         }
         else
         {
         if (this.hasMoved == false)
         {

         }
         }
         }
         return ChessMove;
         */
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        ChessMove move = new ChessMove(myPosition, new ChessPosition(5,5), null);
        pawnMoves.add(move);
        return pawnMoves;
        //throw new RuntimeException("Not implemented");
    }
    public Collection<ChessMove> duckMoves(ChessBoard board, ChessPosition myPosition)
    {
        throw new RuntimeException("Not implemented");
    }
}
