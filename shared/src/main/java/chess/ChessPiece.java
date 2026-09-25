package chess;

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

    //Sets hasMoved to true. Important to know if a pawn can push or if the king can castle
    public static void movePiece(ChessPiece piece)
    {
        piece.hasMoved = true;
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
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor()
    {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType()
    {
        return type;
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
        return moves;
    }

    //handles movement for rooks, bishops, and queens
    public Collection<ChessMove> linearMoves(ChessBoard board, ChessPosition myPosition, int[][] directions)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();
        for(int[] dir:directions)
        {
            int row = myPosition.getRow() + dir[0]; int col = myPosition.getColumn() + dir[1];
            while (bounds(row, col))
            {
                ChessPiece target = board.getPiece(row,col);
                if(target == null)
                    moves.add(new ChessMove(myPosition,new ChessPosition(row,col)));
                else
                {
                    if(target.getTeamColor() != color)
                        moves.add(new ChessMove(myPosition,new ChessPosition(row,col)));
                    break;
                }
                row += dir[0]; col += dir[1];
            }
        }
        return moves;
    }

    //handles King and Knight movement
    public Collection<ChessMove> nonLinearMoves(ChessBoard board, ChessPosition myPosition, int[][] directions)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();
        for(int[] dir:directions)
        {
            int row = myPosition.getRow() + dir[0]; int col = myPosition.getColumn() + dir[1];
            if(bounds(row,col))
            {
                ChessPiece target = board.getPiece(row,col);
                if(target == null || target.getTeamColor() != color)
                    moves.add(new ChessMove(myPosition,new ChessPosition(row,col)));
            }
        }
        return moves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition)
    {
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
        return linearMoves(board,myPosition,directions);
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition)
    {
        int[][] directions = {{-1,1},{-1,-1},{1,-1},{1,1}};
        return linearMoves(board,myPosition,directions);
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> queenMoves = new ArrayList<>();
        queenMoves.addAll(rookMoves(board,myPosition));
        queenMoves.addAll(bishopMoves(board,myPosition));
        return queenMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition)
    {
        int[][] directions = {{2,1},{2,-1},{1,2},{1,-2},{-1,2},{-1,-2},{-2,1},{-2,-1}};
        return nonLinearMoves(board,myPosition,directions);
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition)
    {
        int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1}};
        return nonLinearMoves(board,myPosition,directions);
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
                if(bounds(row-1,col) && board.getPiece(new ChessPosition(row-1, col)) == null)
                {
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col)));
                    if (bounds(row-2,col) && board.getPiece(new ChessPosition(row - 2, col)) == null)
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col)));
                }
            }
            else//moving forward if the pawn has moved
            {
                if(bounds(row-1,col) && board.getPiece(new ChessPosition(row-1, col)) == null)
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col)));
            }
            //capturing pieces
            if(bounds(row-1,col-1) && board.getPiece(row-1, col-1) != null && board.getPiece(row-1, col-1).pieceColor != color)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row-1,col-1)));
            if(bounds(row-1,col+1) && board.getPiece(row-1, col+1) != null && board.getPiece(row-1, col+1).pieceColor != color)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row-1,col+1)));
            //en passant
            if(bounds(row, col + 1) && board.getPiece(row,col+1) != null && board.getPiece(row,col+1).type == PAWN && board.getPiece(row,col+1).pushed)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row,col+1)));
            if(bounds(row, col - 1) && board.getPiece(row,col-1) != null && board.getPiece(row,col-1).type == PAWN && board.getPiece(row,col-1).pushed)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row,col-1)));
        }
        else if(color == WHITE)
        {
            if(row == 2)//moving forward if the pawn hasn't been moved
            {
                if(bounds(row - 1,col) &&bounds(row-1,col) && board.getPiece(new ChessPosition(row+1, col)) == null)
                {
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col)));
                    if (bounds(row + 2,col) && board.getPiece(new ChessPosition(row + 2, col)) == null)
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col)));
                }
            }
            else//moving forward if the pawn has moved
            {
                if(bounds(row+1,col) && board.getPiece(new ChessPosition(row + 1, col)) == null)
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col)));
            }
            //capturing pieces
            if(bounds(row + 1,col-1) && board.getPiece(row + 1, col-1) != null && board.getPiece(row + 1, col-1).pieceColor != color)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1,col-1)));
            if(bounds(row + 1,col+1) && board.getPiece(row + 1, col+1) != null && board.getPiece(row + 1, col+1).pieceColor != color)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1,col+1)));
            //en passant
            if(bounds(row, col + 1) && board.getPiece(row,col+1) != null && board.getPiece(row,col+1).type == PAWN && board.getPiece(row,col+1).pushed)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row,col+1)));
            if(bounds(row, col - 1) && board.getPiece(row,col-1) != null && board.getPiece(row,col-1).type == PAWN && board.getPiece(row,col-1).pushed)
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row,col-1)));
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
    //Only need this because of en passant
    public static void pushPawn(ChessPiece piece)
    {
        piece.pushed = true;
    }
    public static void unpushPawn(ChessPiece piece)
    {
        piece.pushed = false;
    }

    //checks if in bounds
    public boolean bounds(int row, int col)
    {
        return row < 9 && row > 0 && col < 9 && col > 0;
    }
}