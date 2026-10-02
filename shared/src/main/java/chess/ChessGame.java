package chess;

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
    private int turnCount;

    public ChessGame()
    {
        whiteTurn = true;
        board = new ChessBoard();
        board.resetBoard();
        turnCount = 0;
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
    //returns turn count
    public int getTurnCount()
    {
        return turnCount;
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
                int row = startPosition.row() + dir[0]; int col = startPosition.column() + dir[1];
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
                if(enPassantHelper(color, move.getEndPosition()))
                {
                    boolean wasPushed;
                    if(color == WHITE)
                        wasPushed = getPush(board.getPiece(move.getEndPosition().row()-1, move.getEndPosition().column()));
                    else
                        wasPushed = getPush(board.getPiece(move.getEndPosition().row()+1, move.getEndPosition().column()));
                    enPassant(move, color);
                    if(!isInCheck(color, kingPosition))
                        validMoves.add(move);
                    unEnPassant(move, color, wasPushed);
                }
                else
                {
                    ChessPiece capture = board.getPiece(move.getEndPosition());
                    boolean hasMoved = getMoved(piece);
                    boolean wasPawn = (type == PAWN);
                    tryMove(move);
                    if (!isInCheck(color, kingPosition))
                        validMoves.add(move);
                    unMakeMove(move, capture, wasPawn, hasMoved);
                }
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
            throw new InvalidMoveException();//no piece to move
        if(board.getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn())
            throw new InvalidMoveException();//color doesn't match whose turn it is
        if(!validMoves(move.getStartPosition()).contains(move))
            throw new InvalidMoveException();//move not in valid moves
        else
        {
            ChessPosition end = move.getEndPosition();
            ChessPosition start = move.getStartPosition();

            ChessPiece piece = board.getPiece(start);
            PieceType type = piece.getPieceType();
            TeamColor color = piece.getTeamColor();
            PieceType promotion = move.getPromotionPiece();

            unPushAllPieces(color);

            if(enPassantHelper(color, move.getEndPosition()))
                enPassant(move, color);
            else
            {
                if (type == PAWN && promotion != null)
                {
                    board.addPiece(end, new ChessPiece(color, promotion));
                }
                else
                    board.addPiece(end, piece);
                board.removePiece(start);
            }

            if(type == KING && abs(end.column()- start.column()) > 1)
                castleRooks(end);
            else if(type == PAWN && abs(end.row() - start.row()) > 1)
                pushPawn(piece);
            movePiece(piece);
            switchTurn();
            turnCount++;
        }
    }
    //make a chess move to see if King is in check
    public void tryMove(ChessMove move)
    {
        ChessPosition end = move.getEndPosition();
        ChessPosition start = move.getStartPosition();

        ChessPiece piece = board.getPiece(start);
        PieceType type = piece.getPieceType();
        TeamColor color = piece.getTeamColor();
        PieceType promotion = move.getPromotionPiece();

        unPushAllPieces(color);

        if(type == PAWN && promotion != null)
        {
            board.addPiece(end, new ChessPiece(color, promotion));
        }
        else
            board.addPiece(end,piece);
        board.removePiece(start);

        if(type == KING && abs(end.column()- start.column()) > 1)
            castleRooks(end);
        else if(type == PAWN && abs(end.row() - start.row()) > 1)
            pushPawn(piece);
        movePiece(piece);
    }
    //Sets pushed of all pieces of one color to false for En Passant checks
    public void unPushAllPieces(TeamColor color)
    {
            for(int i = 1; i < 9;i++)
                for (int x = 1; x < 9; x++)
                {
                    if(board.getPiece(i,x) != null && board.getPiece(i,x).getTeamColor() == color)
                        unPushPawn(board.getPiece(i,x));
                }
    }
    //Restores board position from before tryMove
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
        if(board.getPiece(start).getPieceType() == KING && abs(end.column()- start.column()) > 1)
            unCastleRooks(start);
        setMoved(board.getPiece(end), hasMoved);
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
    //Checks from rooks, bishops, and queens
    public boolean linearChecks(ChessPosition myPosition, int[][] directions, ChessPiece.PieceType type, TeamColor color)
    {
        for(int[] dir:directions)
        {
            int row = myPosition.row() + dir[0]; int col = myPosition.column() + dir[1];
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
    //Checks from knights
    public boolean nonLinearChecks(ChessPosition myPosition, int[][] directions, TeamColor color)
    {
        for(int[] dir:directions)
        {
            int row = myPosition.row() + dir[0]; int col = myPosition.column() + dir[1];
            if(bounds(row,col))
            {
                ChessPiece target = board.getPiece(row,col);
                if(target != null && target.getTeamColor() != color && target.getPieceType() == KNIGHT)
                    return true;
            }
        }
        return false;
    }
    //checks from pawns
    public boolean pawnChecks(ChessPosition myPosition, TeamColor color)
    {
        int row = myPosition.row(); int col = myPosition.column();
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
    //returns true if a position is in bounds of the chess board
    public boolean bounds(int row, int col) {return row < 9 && row > 0 && col < 9 && col > 0;}
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
    //returns true if the opposite king is blocking a move
    public boolean kingBlocks(ChessPosition myPosition, TeamColor color)
    {
        int[][] directions = {{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};
        for(int[] dir:directions)
        {
            int row = myPosition.row() + dir[0]; int col = myPosition.column() + dir[1];
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
    //returns valid castling moves for the king
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
    //moves the rooks if the king castles
    public void castleRooks(ChessPosition end)
    {
        if(end.row() == 1)
        {
            if(end.column() == 3)
            {
                board.addPiece(1,4, new ChessPiece(WHITE,ROOK));
                board.removePiece(1,1);
            }
            else
            {
                board.addPiece(1,6, new ChessPiece(WHITE,ROOK));
                board.removePiece(1,8);
            }
        }
        else
        {
            if(end.column() == 3)
            {
                board.addPiece(8,4, new ChessPiece(BLACK,ROOK));
                board.removePiece(8,1);
            }
            else
            {
                board.addPiece(8,6, new ChessPiece(BLACK,ROOK));
                board.removePiece(8,8);
            }
        }
    }
    //returns rooks to original corners
    public void unCastleRooks(ChessPosition start)
    {
        if(start.row() == 1)
        {
            if(start.column() == 3)
            {
                board.removePiece(1,4);
                board.addPiece(1,1, new ChessPiece(WHITE,ROOK));
            }
            else
            {
                board.removePiece(1,6);
                board.addPiece(1,8, new ChessPiece(WHITE,ROOK));
            }
        }
        else
        {
            if(start.column() == 3)
            {
                board.removePiece(8,4);
                board.addPiece(8,1, new ChessPiece(BLACK,ROOK));
            }
            else
            {
                board.removePiece(8,6);
                board.addPiece(8,8, new ChessPiece(BLACK,ROOK));
            }
        }
    }
    //preforms En Passant given the move and team color
    public void enPassant(ChessMove move, TeamColor color)
    {
        ChessPosition end = move.getEndPosition();
        ChessPosition start = move.getStartPosition();

        ChessPiece piece = board.getPiece(start);

        unPushAllPieces(color);

        board.addPiece(end,piece);
        board.removePiece(start);
        if(color == WHITE)
            board.removePiece(move.getEndPosition().row()-1, move.getEndPosition().column());
        else
            board.removePiece(move.getEndPosition().row()+1, move.getEndPosition().column());
        switchTurn();
    }
    //reverse reverse
    public void unEnPassant(ChessMove move, TeamColor color, boolean wasPushed)
    {
        ChessPosition end = move.getEndPosition();
        ChessPosition start = move.getStartPosition();

        ChessPiece piece = board.getPiece(end);

        board.addPiece(start,piece);
        board.removePiece(end);
        if(color == WHITE)
        {
            board.addPiece(end.row()-1, end.column(), new ChessPiece(BLACK, PAWN));
            setPush(board.getPiece(end.row()-1, end.column()), wasPushed);
        }
        else
        {
            board.addPiece(move.getEndPosition().row() + 1, move.getEndPosition().column(), new ChessPiece(WHITE, PAWN));
            setPush(board.getPiece(end.row() + 1, end.column()), wasPushed);
        }
        switchTurn();
    }
    //checks true if the move was enpassant, false if it was a normal capture
    public boolean enPassantHelper(TeamColor color, ChessPosition end)
    {
        int endRow = end.row(); int endCol = end.column();
        if(color == WHITE && end.row() == 6)
        {
            ChessPiece target = board.getPiece(endRow-1,endCol);
            return (target != null && target.getPieceType() == PAWN && getPush(target));
        }
        else if(color == BLACK && end.row() == 3)
        {
            ChessPiece target = board.getPiece(endRow+1,endCol);
            return (target != null && target.getPieceType() == PAWN && getPush(target));
        }
        return false;
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
        return Objects.hash(whiteTurn, board, turnCount) * 17;
    }
}