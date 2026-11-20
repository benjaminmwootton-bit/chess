package server.websocket;

import chess.*;
import dataaccess.DataAccess;
import dataaccess.sql.SequelDataAccess;
import io.javalin.websocket.WsConfig;
import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import websocket.commands.UserGameCommand;
import jakarta.websocket.Session;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static chess.ChessGame.TeamColor.WHITE;
import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler {

    private final ConnectionManager connectionManager;
    private final DataAccess dataAccess;

    public WebSocketHandler(ConnectionManager connectionManager) throws Exception {
        this.connectionManager = connectionManager;
        this.dataAccess = new SequelDataAccess();
    }

    public void configure(WsConfig ws) {

        ws.onConnect(ctx -> {
            System.out.println("WebSocket client connected");
        });

        ws.onMessage(ctx -> {
            String json = ctx.message();
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(json, UserGameCommand.class);
            commandHelper(command, ctx);
        });

        ws.onClose(ctx -> {
            System.out.println("WebSocket client disconnected");
        });

        ws.onError(ctx -> {
            System.out.println("WebSocket error: " + ctx.error());
        });
    }

    private void commandHelper(UserGameCommand command, WsMessageContext ctx) throws IOException {
        if(command.getCommandType() == CONNECT) {
            connectHelper(command, ctx);}
        else if (command.getCommandType() == MAKE_MOVE) {
            moveHelper(command);}
        else if (command.getCommandType() == LEAVE) {
            resignAndLeaveHelper(command, true);}
        else if (command.getCommandType() == RESIGN) {
            resignAndLeaveHelper(command, false);}
        else if(command.getCommandType() == REDRAW){
            redrawHelper(command);}
        else if(command.getCommandType() == HIGHLIGHT){
            highlightHelper(command);}
    }


    private void connectHelper(UserGameCommand command, WsMessageContext ctx){
        String authToken = command.getAuthToken();
        if(authToken == null){
            System.out.println("Not authorized");
            return;
        }

        String username = dataAccess.getUsernameWithAuthToken(authToken);
        if(username == null){
            System.out.println("Username doesn't exist");
            return;
        }

        GameData game = dataAccess.getGame(command.getGameID());
        if(game == null){
            System.out.println("Game doesn't exist");
            return;
        }

        ChessGame.TeamColor teamColor;
        if(username.equals(game.whiteUsername())){
            teamColor = ChessGame.TeamColor.WHITE;}
        else if(username.equals(game.blackUsername())){
            teamColor = ChessGame.TeamColor.BLACK;}
        else{
            teamColor = null;}

        int gameID = game.gameID();

        connectionManager.addConnection(gameID,new Connection(username, (Session) ctx.session,gameID, teamColor));
    }

    private void highlightHelper(UserGameCommand command){
        int gameID = command.getGameID();
        GameData game = dataAccess.getGame(gameID);
        if(game == null) {
            System.out.println("Game doesn't exist");
            return;
        }
        ChessGame chessGame = game.chessGame();
        int[] pos = command.getSquare();
        Collection<ChessMove> moves = chessGame.validMoves(new ChessPosition(pos[0], pos[1]));

        String authToken = command.getAuthToken();
        if(authToken == null){
            System.out.println("Not authorized");
            return;
        }

        String username = dataAccess.getUsernameWithAuthToken(authToken);
        if(username == null){
            System.out.println("Username doesn't exist");
            return;
        }
        if(username.equals(game.blackUsername())){
            System.out.println(highlightedBoardBlack(chessGame.getBoard(), moves));}
        else{
            System.out.println(highlightedBoard(chessGame.getBoard(), moves));}
    }

    private void moveHelper(UserGameCommand command) throws IOException{
        int gameID = command.getGameID();
        GameData game = dataAccess.getGame(gameID);
        if(game == null) {
            System.out.println("Game doesn't exist");
            return;
        }
        ChessGame chessGame = game.chessGame();
        try{
            chessGame.makeMove(command.getMove());
        }
        catch (InvalidMoveException e) {
            System.out.println(e.getMessage());}

        redrawHelper(command);
    }

    private void redrawHelper(UserGameCommand command) throws IOException{
        int gameID = command.getGameID();
        GameData game = dataAccess.getGame(gameID);
        if(game == null){
            System.out.println("Game doesn't exist");
            return;
        }

        String authToken = command.getAuthToken();
        if(authToken == null){
            System.out.println("Not authorized");
            return;
        }

        String username = dataAccess.getUsernameWithAuthToken(authToken);
        if(username == null){
            System.out.println("Username doesn't exist");
            return;
        }
        if(username.equals(game.blackUsername())){
            System.out.println(dataAccess.getGame(command.getGameID()).chessGame().toStringBlack());}
        else{
            System.out.println(dataAccess.getGame(command.getGameID()).chessGame());}
    }

    //still need to disable game
    private void resignAndLeaveHelper(UserGameCommand command, boolean leave) throws IOException {
        int gameID = command.getGameID();
        GameData game = dataAccess.getGame(gameID);
        if(game == null){
            System.out.println("Game doesn't exist");
            return;
        }

        String authToken = command.getAuthToken();
        if(authToken == null){
            System.out.println("Not authorized");
            return;
        }

        String username = dataAccess.getUsernameWithAuthToken(authToken);
        if(username == null){
            System.out.println("Username doesn't exist");
            return;
        }

        Connection connection = null;
        Set<Connection> cons = connectionManager.getConnections(gameID);
        if (cons != null) {
            for (Connection c : cons) {
                if (c.username().equals(username)) {
                    connection = c;
                    break;
                }
            }
        }
        if (connection != null) {
            connectionManager.removeConnection(gameID, connection);}
        else{
            return;}

        Set<Connection> rest = connectionManager.getConnections(gameID);
        ServerMessage message;
        if(leave){
            message = new ServerMessage(ServerMessageType.NOTIFICATION, username + " has left the game");
        }
        else{
            message = new ServerMessage(ServerMessageType.NOTIFICATION, username + " has resigned the game");}

        Gson gson = new Gson();
        String json = gson.toJson(message);

        if (rest != null) {
            for (Connection c : rest) {
                c.session().getBasicRemote().sendText(json);
            }
        }
        if(!leave){
            connection.session().getBasicRemote().sendText(json);}
    }

    public String highlightedBoard(ChessBoard board, Collection<ChessMove> squares)
    {
        StringBuilder str = new StringBuilder();
        str.append("\u001b[31;100;1m    a  b  c  d  e  f  g  h    \u001b[0m\n");
        for (int i = 7; i >= 0; i--) {
            str.append("\u001b[31;100;1m ").append(i + 1).append(" \u001b[0m");
            for (int x = 0; x < 8; x++) {
                str.append(toStringHelper(i, x, board, squares));}
            str.append("\u001b[31;100;1m ").append(i + 1).append(" \u001b[0m\n");
        }
        str.append("\u001b[31;100;1m    a  b  c  d  e  f  g  h    \u001b[0m");
        return str.toString();
    }

    public String highlightedBoardBlack(ChessBoard board, Collection<ChessMove> squares) {
        StringBuilder str = new StringBuilder();
        str.append("\u001b[31;100;1m    h  g  f  e  d  c  b  a    \u001b[0m\n");
        for (int i = 0; i < 8; i++) {
            str.append("\u001b[31;100;1m ").append(i + 1).append(" ");
            for (int x = 7; x >= 0; x--) {
                str.append(toStringHelper(i, x, board, squares));}
            str.append("\u001b[31;100;1m ").append(i + 1).append(" \u001b[0m\n");
        }
        str.append("\u001b[31;100;1m    h  g  f  e  d  c  b  a    \u001b[0m");
        return str.toString();
    }

    public String toStringHelper(int i, int x, ChessBoard board, Collection<ChessMove> squares)
    {
        StringBuilder str = new StringBuilder();
        if ((i + x) % 2 != 0) {
            str.append("\u001b[104m");}
        else {
            str.append("\u001b[44m");}
        if (board.getPiece(i,x) != null) {
            if (board.getPiece(i,x).getTeamColor() == WHITE) {
                str.append("\u001b[31m");}
            else {
                str.append("\u001b[30m");}
            str.append(" ");
            str.append(board.getPiece(i,x));
        }
        else {
            str.append("  ");}
        str.append(" ");
        str.append("\u001b[0m");
        return str.toString();
    }
}