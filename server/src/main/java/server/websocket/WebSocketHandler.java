package server.websocket;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.sql.SequelDataAccess;
import io.javalin.websocket.WsConfig;
import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import service.Service;
import websocket.commands.UserGameCommand;
import jakarta.websocket.Session;

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

    private void commandHelper(UserGameCommand command, WsMessageContext ctx){
        if(command.getCommandType() == CONNECT)
        {
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
        else if (command.getCommandType() == MAKE_MOVE)
        {

        }
        else if (command.getCommandType() == LEAVE)
        {

        }
        else if (command.getCommandType() == RESIGN)
        {

        }
    }
}