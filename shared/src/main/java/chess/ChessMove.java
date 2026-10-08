package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove
{
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType piece;
    //constructors
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.piece = promotionPiece;
    }
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.piece = null;
    }
    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition()
    {
        return this.startPosition;
    }
    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition()
    {
        return this.endPosition;
    }
    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece()
    {
        return this.piece;
    }
    //(very) simplified chess notation
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        String[] col = {"a", "b", "c", "d", "e", "f", "g", "h"};
        str.append(col[startPosition.column() - 1]).append(startPosition.row());
        str.append(col[endPosition.column() - 1]).append(endPosition.row());
        if(piece != null)
            str.append("=").append(piece);
        return str.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass())
            return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && piece == chessMove.piece;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(startPosition, endPosition, piece) * 17;
    }
}