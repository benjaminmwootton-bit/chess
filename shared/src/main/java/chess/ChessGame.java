package chess;

import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;
import static chess.ChessPiece.*;
import static java.lang.Math.abs;

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
        board.resetBoard();
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
    public ChessBoard getBoard() {return board;}
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
        whiteTurn = (team == WHITE);
    }
    public void switchTurn(){whiteTurn = !whiteTurn;};
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        var type = piece.getPieceType(); var color = piece.getTeamColor();
        if(type == KING)
        {
            int[][] kingDirections = {{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};
            for(int[] dir:kingDirections)
            {
                int row = startPosition.getRow() + dir[0]; int col = startPosition.getColumn() + dir[1];
                if(bounds(row,col))
                {
                    ChessPiece target = board.getPiece(row, col);
                    ChessPosition pos = new ChessPosition(row, col);
                    if (!kingBlocks(pos, color) && !isInCheck(color, pos) && (target == null || target.getTeamColor() != piece.getTeamColor()))
                        validMoves.add(new ChessMove(startPosition, pos));
                }
            }
            validMoves.addAll(castle(piece,color,startPosition));
        }
        else
        {
            Collection<ChessMove> possibleMoves = piece.pieceMoves(board,startPosition);
            ChessPosition kingPosition = kingPosition(color);
            for (ChessMove move: possibleMoves)
            {
                ChessPiece capture = board.getPiece(move.getEndPosition());
                boolean hasMoved = getMoved(piece);
                boolean wasPawn = (type == PAWN);
                moveMake(move);
                if(!isInCheck(color,kingPosition))
                    validMoves.add(move);
                unMakeMove(move, capture, wasPawn, hasMoved);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException
    {
        if (board.getPiece(move.getStartPosition()) == null)
            throw new InvalidMoveException();
        if(board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn())
            throw new InvalidMoveException();
        if(!validMoves(move.getStartPosition()).contains(move))
            throw new InvalidMoveException();
        else
        {
            ChessPosition end = move.getEndPosition();
            ChessPosition start = move.getStartPosition();

            ChessPiece piece = board.getPiece(start);
            PieceType type = piece.getPieceType();
            TeamColor color = piece.getTeamColor();
            PieceType promotion = move.getPromotionPiece();

            unPushAllPieces(piece, color);

            if(type == PAWN && promotion != null)
            {
                board.addPiece(end, new ChessPiece(color, promotion));
            }
            else
                board.addPiece(end,piece);
            board.removePiece(start);

            if(type == PAWN && abs(end.getRow() - start.getRow()) > 1)
                pushPawn(piece);
            movePiece(piece);
            switchTurn();
        }
    }

    public void moveMake(ChessMove move)
    {
        ChessPosition end = move.getEndPosition();
        ChessPosition start = move.getStartPosition();

        ChessPiece piece = board.getPiece(start);
        PieceType type = piece.getPieceType();
        TeamColor color = piece.getTeamColor();
        PieceType promotion = move.getPromotionPiece();

        unPushAllPieces(piece, color);

        if(type == PAWN && promotion != null)
        {
            board.addPiece(end, new ChessPiece(color, promotion));
        }
        else
            board.addPiece(end,piece);
        board.removePiece(start);

        if(type == PAWN && abs(end.getRow() - start.getRow()) > 1)
            pushPawn(piece);
        movePiece(piece);
        switchTurn();
    }

    public void unPushAllPieces(ChessPiece piece, TeamColor color)
    {
            for(int i = 1; i < 9;i++)
                for (int x = 1; x < 9; x++)
                {
                    if(board.getPiece(i,x) != null && board.getPiece(i,x).getTeamColor() == color)
                        unPushPawn(piece);
                }
    }

    public void unMakeMove(ChessMove move, ChessPiece capture, boolean wasPawn, boolean hasMoved)
    {
        ChessPosition end = move.getEndPosition();
        ChessPosition start = move.getStartPosition();
        if(wasPawn)
            board.addPiece(start, new ChessPiece(board.getPiece(end).getTeamColor(), PAWN));
        else
            board.addPiece(start,board.getPiece(end));
        if(capture != null)
            board.addPiece(end, capture);
        else
            board.removePiece(end);
        setMoved(board.getPiece(end), hasMoved);
        switchTurn();
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
        return isInCheck(teamColor, kingPosition);
    }
    public boolean isInCheck(TeamColor teamColor, ChessPosition kingPosition)
    {
        int[][] KnightDirections = {{2,1},{2,-1},{1,2},{1,-2},{-1,2},{-1,-2},{-2,1},{-2,-1}};
        if (nonLinearChecks(kingPosition,KnightDirections,teamColor))
            return true;
        int[][] rookDirections = {{0,1},{1,0},{0,-1},{-1,0}};
        if (linearChecks(kingPosition,rookDirections,ROOK,teamColor))
            return true;
        int[][] bishopDirections = {{-1,1},{1,-1},{-1,-1},{1,1}};
        if (linearChecks(kingPosition,bishopDirections,BISHOP,teamColor))
            return true;
        return (pawnChecks(kingPosition, teamColor));
    }

    public boolean linearChecks(ChessPosition myPosition, int[][] directions, ChessPiece.PieceType type, TeamColor color)
    {
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

    public boolean nonLinearChecks(ChessPosition myPosition, int[][] directions, TeamColor color)
    {
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

    public boolean pawnChecks(ChessPosition myPosition, TeamColor color)
    {
        int row = myPosition.getRow(); int col = myPosition.getColumn();
        if(color == WHITE)
        {
            if (bounds(row+1,col+1))
            {
                ChessPiece target = board.getPiece(row+1,col+1);
                if (target != null && target.getPieceType() == PAWN && target.getTeamColor() != color)
                    return true;
            }
            if(bounds(row+1,col-1))
            {
                ChessPiece target = board.getPiece(row+1,col-1);
                return (target != null && target.getPieceType() == PAWN && target.getTeamColor() != color);
            }
        }
        else if (color == BLACK)
        {
            if (bounds(row-1,col+1))
            {
                ChessPiece target = board.getPiece(row-1,col+1);
                if (target != null && target.getPieceType() == PAWN && target.getTeamColor() != color)
                    return true;
            }
            if(bounds(row-1,col-1))
            {
                ChessPiece target = board.getPiece(row-1,col-1);
                return (target != null && target.getPieceType() == PAWN && target.getTeamColor() != color);
            }
        }
        return false;
    }

    public boolean bounds(int row, int col)
    {
        return row < 9 && row > 0 && col < 9 && col > 0;
    }
    public boolean bounds(ChessPosition pos)
    {
        int row = pos.getRow(); int col = pos.getColumn();
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
        if(!isInCheck(teamColor))
            return false;
        for(int i = 1; i < 9;i++)
            for (int x = 1; x < 9; x++)
            {
                ChessPiece piece = board.getPiece(i,x);
                if(piece != null && piece.getTeamColor() == teamColor && !validMoves(new ChessPosition(i,x)).isEmpty())
                    return false;
            }
        return true;
    }
    public boolean kingBlocks(ChessPosition myPosition, TeamColor color)
    {
        int[][] directions = {{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};
        for(int[] dir:directions)
        {
            int row = myPosition.getRow() + dir[0]; int col = myPosition.getColumn() + dir[1];
            if(bounds(row,col))
            {
                ChessPiece target = board.getPiece(row,col);
                if(target != null && target.getPieceType() == KING && target.getTeamColor() != color)
                    return true;
            }
        }
        return false;
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
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor)
    {
        ChessPosition kingPosition = kingPosition(teamColor);
        if(isInCheck(teamColor, kingPosition))
            return false;
        for(int i = 1; i < 9;i++)
            for (int x = 1; x < 9; x++)
            {
                ChessPiece piece = board.getPiece(i,x);
                if(piece != null && piece.getPieceType() != KING && piece.getTeamColor() == teamColor)
                {
                    Collection<ChessMove> moves1 = validMoves(new ChessPosition(i,x));
                    if(!moves1.isEmpty())
                        return false;
                }
            }
        return true;
    }

    public Collection<ChessMove> castle(ChessPiece king, TeamColor color, ChessPosition kingPosition)
    {
        Collection<ChessMove> castleMoves = new ArrayList<>();
        if(!getMoved(king) && !isInCheck(color, kingPosition))
        {
            if (color == BLACK)
            {
                int[][] squares1 = {{8,3},{8,4}};
                ChessPiece rook1 = board.getPiece(8,1);
                if(rook1 != null && !getMoved(rook1) && board.getPiece(8,2) == null && castleHelper(color,squares1))
                    castleMoves.add(new ChessMove(kingPosition,new ChessPosition(8,3)));
                int[][] squares2 = {{8,6},{8,7}};
                ChessPiece rook2 = board.getPiece(8,8);
                if(rook1 != null && !getMoved(rook2) && castleHelper(color,squares2))
                    castleMoves.add(new ChessMove(kingPosition,new ChessPosition(8,7)));
            }
            else
            {
                int[][] squares1 = {{1,3},{1,4}};
                ChessPiece rook1 = board.getPiece(1,1);
                if(rook1 != null && !getMoved(rook1) && board.getPiece(1,2) == null && castleHelper(color,squares1))
                    castleMoves.add(new ChessMove(kingPosition,new ChessPosition(1,3)));
                int[][] squares2 = {{1,6},{1,7}};
                ChessPiece rook2 = board.getPiece(1,8);
                if(rook1 != null && !getMoved(rook2) && castleHelper(color,squares2))
                    castleMoves.add(new ChessMove(kingPosition,new ChessPosition(1,7)));
            }
        }
        return castleMoves;
    }
    public boolean castleHelper(TeamColor color, int[][] squares)
    {
        for(int[] square:squares)
        {
            if(board.getPiece(square[0],square[1]) != null || isInCheck(color, new ChessPosition(square[0],square[1])))
                return false;
        }
        return true;
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