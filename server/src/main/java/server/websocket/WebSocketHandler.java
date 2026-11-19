package server.websocket;

import io.javalin.websocket.WsConfig;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {

    public void configure(WsConfig ws) {

        ws.onConnect(ctx -> {
            System.out.println("WebSocket client connected");
        });

        ws.onMessage(ctx -> {
            String json = ctx.message();
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(json, UserGameCommand.class);
            //System.out.println("Received command: " + command.getCommandType());
            //System.out.println("Game ID: " + command.getGameID());
        });

        ws.onClose(ctx -> {
            System.out.println("WebSocket client disconnected");
        });

        ws.onError(ctx -> {
            System.out.println("WebSocket error: " + ctx.error());
        });
    }
}