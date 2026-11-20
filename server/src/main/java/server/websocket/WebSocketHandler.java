package server.websocket;

import chess.ChessGame;
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
import java.util.Set;

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
        }
        else if (command.getCommandType() == LEAVE) {
        leaveHelper(command, ctx);}
        else if (command.getCommandType() == RESIGN) {
        }
    }

    private void leaveHelper(UserGameCommand command, WsMessageContext ctx) throws IOException {
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
        ServerMessage message = new ServerMessage(ServerMessageType.NOTIFICATION, username + " has left the game");

        Gson gson = new Gson();
        String json = gson.toJson(message);

        if (rest != null) {
            for (Connection c : rest) {
                c.session().getBasicRemote().sendText(json);
            }
        }
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
}