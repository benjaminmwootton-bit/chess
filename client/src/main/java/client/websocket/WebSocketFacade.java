package client.websocket;

import chess.ChessMove;
import client.State;
import com.google.gson.Gson;

import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(
                (MessageHandler.Whole<String>) message -> {
                    Gson gson = new Gson();
                    ServerMessage serverMessage =
                            gson.fromJson(message, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
        );
    }

    public void sendMessage(UserGameCommand command) throws ResponseException {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(command);
            session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void close() throws ResponseException, IOException {
        if(session.isOpen()){
            session.close();}
    }

    public void connect(String authToken, Integer gameID) throws ResponseException, IOException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        sendMessage(command);
    }

    public State redraw(String authToken, int gameID) throws ResponseException {
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.REDRAW, authToken, gameID);
        sendMessage(cmd);
        return State.INGAME;
    }

    public State move(String authToken, Integer gameID, String move){return State.INGAME;}

    public State leave(String authToken, Integer gameID){return State.SIGNEDIN;}

    public State resign(String authToken, Integer gameID){return State.SIGNEDIN;}

    public State highlight(String authToken, Integer gameID, String square){return State.INGAME;}
}