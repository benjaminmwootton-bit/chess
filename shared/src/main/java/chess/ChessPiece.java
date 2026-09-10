package chess;

import com.sun.source.tree.WhileLoopTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessPiece.PieceType.BISHOP;
import static chess.ChessPiece.PieceType.KNIGHT;
import static chess.ChessPiece.PieceType.ROOK;
import static chess.ChessPiece.PieceType.PAWN;
import static chess.ChessPiece.PieceType.QUEEN;
import static chess.ChessPiece.PieceType.KING;
import static chess.ChessPiece.PieceType.DUCK;

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
    private boolean pushed;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type)
    {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = false;
        this.pushed = false;
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
            else if (this.type == DUCK)
                return "D";
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
            else if (this.type == DUCK)
                return "D";
            else
                return null;
        }
        else
            return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass())
            return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(pieceColor, type) * 17;
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
        PAWN,
        DUCK
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
            moves = pawnMoves(board, myPosition);
        else if(board.getPiece(myPosition).type == ROOK)
            moves = rookMoves(board, myPosition);
        else if(board.getPiece(myPosition).type == BISHOP)
            moves = bishopMoves(board, myPosition);
        else if(board.getPiece(myPosition).type == KNIGHT)
            moves = knightMoves(board, myPosition);
        else if(board.getPiece(myPosition).type == QUEEN)
            moves = queenMoves(board, myPosition);
        else if(board.getPiece(myPosition).type == KING)
            moves = kingMoves(board, myPosition);
        else if(board.getPiece(myPosition).type == DUCK)
            moves = duckMoves(board, myPosition);
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
        ArrayList<ChessMove> bishopMoves = new ArrayList<>();
        int rowPos = myPosition.getRow(); int colPos = myPosition.getColumn();
        var piece = board.getPiece(rowPos,colPos);
        rowPos--;colPos--; // left down
        while(outOfBounds(rowPos,colPos) && board.getPiece(rowPos,colPos) == null)
        {
            bishopMoves.add(new ChessMove(myPosition,new ChessPosition(rowPos,colPos), null));
            rowPos--;colPos--;
        }
        if (outOfBounds(rowPos,colPos) && board.getPiece(rowPos,colPos) != null && piece.pieceColor != board.getPiece(rowPos,colPos).pieceColor)//capture
            bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos,colPos), null));
        rowPos = myPosition.getRow(); colPos = myPosition.getColumn();//reset

        rowPos++;colPos--; // left up
        while(outOfBounds(rowPos,colPos) && board.getPiece(rowPos,colPos) == null)
        {
            bishopMoves.add(new ChessMove(myPosition,new ChessPosition(rowPos,colPos), null));
            rowPos++;colPos--;
        }
        if (outOfBounds(rowPos,colPos) && board.getPiece(rowPos,colPos) != null && piece.pieceColor != board.getPiece(rowPos,colPos).pieceColor)//capture
            bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos,colPos), null));
        rowPos = myPosition.getRow(); colPos = myPosition.getColumn();//reset

        rowPos++;colPos++; // right up
        while(outOfBounds(rowPos,colPos) && board.getPiece(rowPos,colPos) == null)
        {
            bishopMoves.add(new ChessMove(myPosition,new ChessPosition(rowPos,colPos), null));
            rowPos++;colPos++;
        }
        if (outOfBounds(rowPos,colPos) && board.getPiece(rowPos,colPos) != null && piece.pieceColor != board.getPiece(rowPos,colPos).pieceColor)//capture
            bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos,colPos), null));
        rowPos = myPosition.getRow(); colPos = myPosition.getColumn();//reset

        rowPos--;colPos++; // right down
        while(outOfBounds(rowPos,colPos) && board.getPiece(rowPos,colPos) == null)
        {
            bishopMoves.add(new ChessMove(myPosition,new ChessPosition(rowPos,colPos), null));
            rowPos--;colPos++;
        }
        if (outOfBounds(rowPos,colPos) && board.getPiece(rowPos,colPos) != null && piece.pieceColor != board.getPiece(rowPos,colPos).pieceColor)//capture
            bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos,colPos), null));

        return bishopMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition)
    {//I'm lazy
        ArrayList<ChessMove> kingMoves = new ArrayList<>();
        var rowPos = myPosition.getRow(); int colPos = myPosition.getColumn();
        var color = board.getPiece(myPosition).getTeamColor();
        if(outOfBounds(rowPos -2,colPos-1) && (board.getPiece(rowPos -2, colPos-1) == null || board.getPiece(rowPos-2, colPos-1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos-2,colPos-1), null));
        if(outOfBounds(rowPos-1,colPos-2) && (board.getPiece(rowPos -1, colPos-2) == null || board.getPiece(rowPos-1, colPos-2).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos-1,colPos-2), null));
        if(outOfBounds(rowPos-2,colPos+1) && (board.getPiece(rowPos-2, colPos+1) == null || board.getPiece(rowPos-2, colPos+1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos-2,colPos+1), null));
        if(outOfBounds(rowPos -1,colPos+2) && (board.getPiece(rowPos -1, colPos+2) == null || board.getPiece(rowPos-1, colPos+2).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos-1,colPos+2), null));
        if(outOfBounds(rowPos+1,colPos+2) && (board.getPiece(rowPos+1, colPos+2) == null || board.getPiece(rowPos+1, colPos+2).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos+1,colPos+2), null));
        if(outOfBounds(rowPos+2,colPos+1) && (board.getPiece(rowPos+2, colPos+1) == null || board.getPiece(rowPos+2, colPos+1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos+2,colPos+1), null));
        if(outOfBounds(rowPos+1,colPos-2) && (board.getPiece(rowPos+1, colPos-2) == null || board.getPiece(rowPos+1, colPos-2).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos+1,colPos-2), null));
        if(outOfBounds(rowPos+2,colPos-1) && (board.getPiece(rowPos+2, colPos-1) == null || board.getPiece(rowPos+2, colPos-1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos+2,colPos-1), null));
        return kingMoves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> queenMoves = new ArrayList<>();
        queenMoves.addAll(rookMoves(board,myPosition));
        queenMoves.addAll(bishopMoves(board,myPosition));
        return queenMoves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> kingMoves = new ArrayList<>();
        var rowPos = myPosition.getRow(); int colPos = myPosition.getColumn();
        var color = board.getPiece(myPosition).getTeamColor();
        if(outOfBounds(rowPos -1,colPos) && (board.getPiece(rowPos -1, colPos) == null || board.getPiece(rowPos-1, colPos).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos-1,colPos), null));
        if(outOfBounds(rowPos-1,colPos-1) && (board.getPiece(rowPos -1, colPos-1) == null || board.getPiece(rowPos-1, colPos-1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos-1,colPos-1), null));
        if(outOfBounds(rowPos,colPos-1) && (board.getPiece(rowPos , colPos-1) == null || board.getPiece(rowPos, colPos-1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos,colPos-1), null));
        if(outOfBounds(rowPos +1,colPos) && (board.getPiece(rowPos +1, colPos) == null || board.getPiece(rowPos+1, colPos).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos+1,colPos), null));
        if(outOfBounds(rowPos+1,colPos+1) && (board.getPiece(rowPos+1, colPos+1) == null || board.getPiece(rowPos+1, colPos+1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos+1,colPos+1), null));
        if(outOfBounds(rowPos,colPos+1) && (board.getPiece(rowPos , colPos+1) == null || board.getPiece(rowPos, colPos+1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos,colPos+1), null));
        if(outOfBounds(rowPos-1,colPos+1) && (board.getPiece(rowPos-1, colPos+1) == null || board.getPiece(rowPos-1, colPos+1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos-1,colPos+1), null));
        if(outOfBounds(rowPos+1,colPos-1) && (board.getPiece(rowPos+1, colPos-1) == null || board.getPiece(rowPos+1, colPos-1).pieceColor != color))
            kingMoves.add(new ChessMove(myPosition, new ChessPosition(rowPos+1,colPos-1), null));
        return kingMoves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        ChessPiece pawn = board.getPiece(myPosition);
        var color = pawn.getTeamColor();
        int col = myPosition.getColumn(); int row = myPosition.getRow();
        if(color == BLACK)
        {
            if(row == 7)//moving forward if the pawn hasn't been moved
            {
                if(outOfBounds(row-1,col) && board.getPiece(new ChessPosition(row-1, col)) == null)
                {
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
                    if (outOfBounds(row-2,col) && board.getPiece(new ChessPosition(row - 2, col)) == null)
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col), null));
                }
            }
            else//moving forward if the pawn has moved
            {
                if(outOfBounds(row-1,col) && board.getPiece(new ChessPosition(row-1, col)) == null)
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
            }
            //capturing pieces
            if(outOfBounds(row-1,col-1) && board.getPiece(row-1, col-1) != null && board.getPiece(row-1, col-1).pieceColor != color)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row-1,col-1), null));
            if(outOfBounds(row-1,col+1) && board.getPiece(row-1, col+1) != null && board.getPiece(row-1, col+1).pieceColor != color)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row-1,col+1), null));
            //en passant
            if(outOfBounds(row, col + 1) && board.getPiece(row,col+1) != null && board.getPiece(row,col+1).type == PAWN && board.getPiece(row,col+1).pushed == true)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row,col+1), null));
            if(outOfBounds(row, col - 1) && board.getPiece(row,col-1) != null && board.getPiece(row,col-1).type == PAWN && board.getPiece(row,col-1).pushed == true)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row,col-1), null));
        }
        if(color == WHITE)
        {
            if(row == 2)//moving forward if the pawn hasn't been moved
            {
                if(outOfBounds(row - 1,col) &&outOfBounds(row-1,col) && board.getPiece(new ChessPosition(row+1, col)) == null)
                {
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
                    if (outOfBounds(row + 2,col) && board.getPiece(new ChessPosition(row + 2, col)) == null)
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col), null));
                }
            }
            else//moving forward if the pawn has moved
            {
                if(outOfBounds(row+1,col) && board.getPiece(new ChessPosition(row + 1, col)) == null)
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
            }
            //capturing pieces
            if(outOfBounds(row + 1,col-1) && board.getPiece(row + 1, col-1) != null && board.getPiece(row + 1, col-1).pieceColor != color)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1,col-1), null));
            if(outOfBounds(row + 1,col+1) && board.getPiece(row + 1, col+1) != null && board.getPiece(row + 1, col+1).pieceColor != color)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1,col+1), null));
            //en passant
            if(outOfBounds(row, col + 1) && board.getPiece(row,col+1) != null && board.getPiece(row,col+1).type == PAWN && board.getPiece(row,col+1).pushed == true)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row,col+1), null));
            if(outOfBounds(row, col - 1) && board.getPiece(row,col-1) != null && board.getPiece(row,col-1).type == PAWN && board.getPiece(row,col-1).pushed == true)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row,col-1), null));
        }
        //promotions
        ArrayList<ChessMove> pawnMovesPromotions = new ArrayList<>();
        for(ChessMove move:pawnMoves)
        {
            int tempEnd = move.getEndPosition().getRow();
            if((color == WHITE && tempEnd == 8) || (color == BLACK && tempEnd == 1))
            {
                pawnMovesPromotions.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),QUEEN));
                pawnMovesPromotions.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),ROOK));
                pawnMovesPromotions.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),BISHOP));
                pawnMovesPromotions.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),KNIGHT));
            }
            else
                pawnMovesPromotions.add(move);
        }
        return pawnMovesPromotions;
    }

    //You ever played duck chess? It's fun!
    public Collection<ChessMove> duckMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> duckMoves = new ArrayList<>();
        for (int i = 1; i < 9; i++)
        {
            for (int x = 1; x < 9; x++)
            {
                var newPosition = new ChessPosition(i,x);
                if(board.getPiece(i,x) == null && myPosition != newPosition)
                    duckMoves.add(new ChessMove(myPosition,newPosition,null));
            }
        }
        return duckMoves;
    }

    // returns true if not out of bounds
    public boolean outOfBounds(int row, int col)
    {
        return row < 9 && row > 0 && col < 9 && col > 0;
    }
}
