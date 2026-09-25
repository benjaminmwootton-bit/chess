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
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame
{
    private boolean whiteTurn;
    private ChessBoard board;

    public ChessGame()
    {
        whiteTurn = true;
        board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn()
    {
        if(whiteTurn) return WHITE;
        else return BLACK;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team)
    {
        if(team == WHITE) whiteTurn = true;
        else whiteTurn = false;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor
    {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition)
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException
    {
        ChessPiece.movePiece(board.getPiece(move.getStartPosition()));
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        ChessPosition kingPosition = kingPosition(teamColor);
        int[][] KnightDirections = {{2,1},{2,-1},{1,2},{1,-2},{-1,2},{-1,-2},{-2,1},{-2,-1}};
        if (nonLinearChecks(board,kingPosition,KnightDirections))
            return true;
        int[][] rookDirections = {{0,1},{1,0},{0,-1},{-1,0}};
        if (linearChecks(board,kingPosition,rookDirections,ROOK))
            return true;
        int[][] bishopDirections = {{-1,1},{1,-1},{-1,-1},{1,1}};
        if (linearChecks(board,kingPosition,rookDirections,BISHOP))
            return true;
        return pawnChecks(board,kingPosition);
    }
    public boolean linearChecks(ChessBoard board, ChessPosition myPosition, int[][] directions, ChessPiece.PieceType type)
    {
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();
        for(int[] dir:directions)
        {
            int row = myPosition.getRow() + dir[0]; int col = myPosition.getColumn() + dir[1];
            while (bounds(row, col))
            {
                ChessPiece target = board.getPiece(row,col);
                if(target != null)
                {
                    ChessPiece.PieceType targetType = target.getPieceType();
                    if (target.getTeamColor() != color && (targetType == type || targetType == QUEEN))
                        return true;
                    break;
                }
                row += dir[0]; col += dir[1];
            }
        }
        return false;
    }
    public boolean nonLinearChecks(ChessBoard board, ChessPosition myPosition, int[][] directions)
    {
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();
        for(int[] dir:directions)
        {
            int row = myPosition.getRow() + dir[0]; int col = myPosition.getColumn() + dir[1];
            if(bounds(row,col))
            {
                ChessPiece target = board.getPiece(row,col);
                if(target != null && target.getTeamColor() != color && target.getPieceType() == KNIGHT)
                    return true;
            }
        }
        return false;
    }
    public boolean pawnChecks(ChessBoard board, ChessPosition myPosition)
    {
        ChessPiece king = board.getPiece(myPosition);
        ChessGame.TeamColor color = king.getTeamColor();
        int row = myPosition.getRow(); int col = myPosition.getColumn();
        if(color == WHITE)
        {
            if (bounds(row+1,col+1))
            {
                ChessPiece target = board.getPiece(row+1,col+1);
                if (target.getPieceType() == PAWN && target.getTeamColor() != color)
                    return true;
            }
            if(bounds(row+1,col-1))
            {
                ChessPiece target = board.getPiece(row+1,col+1);
                if (target.getPieceType() == PAWN && target.getTeamColor() != color)
                    return true;
            }
        }
        else if (color == BLACK)
        {
            if (bounds(row-1,col+1))
            {
                ChessPiece target = board.getPiece(row+1,col+1);
                if (target != null && target.getPieceType() == PAWN && target.getTeamColor() != color)
                    return true;
            }
            if(bounds(row-1,col-1))
            {
                ChessPiece target = board.getPiece(row+1,col+1);
                return (target != null && target.getPieceType() == PAWN && target.getTeamColor() != color);
            }
        }
        return false;
    }
    public boolean bounds(int row, int col)
    {
        return row < 9 && row > 0 && col < 9 && col > 0;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor)
    {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor)
    {
        throw new RuntimeException("Not implemented");
    }

    //returns the position of a team's King
    public ChessPosition kingPosition(TeamColor teamColor)
    {
        for(int i = 1; i < 9;i++)
            for (int x = 1; x < 9; x++)
            {
                ChessPiece piece = board.getPiece(i,x);
                if (piece != null && piece.getPieceType() == KING && piece.getTeamColor() == teamColor)
                    return new ChessPosition(i,x);
            }
        return null;
    }
    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board)
    {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard()
    {
        return board;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass())
            return false;
        ChessGame chessGame = (ChessGame) o;
        return whiteTurn == chessGame.whiteTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(whiteTurn, board) * 17;
    }
}
