package server.websocket;

import io.javalin.websocket.WsConfig;
import com.google.gson.Gson;
import service.Service;
import websocket.commands.UserGameCommand;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler {

    private final Service service;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(Service service) {
        this.service = service;
    }

    public void configure(WsConfig ws) {

        ws.onConnect(ctx -> {
            System.out.println("WebSocket client connected");
        });

        ws.onMessage(ctx -> {
            String json = ctx.message();
            Gson gson = new Gson();
            UserGameCommand command = gson.fromJson(json, UserGameCommand.class);
            commandHelper(command);
        });

        ws.onClose(ctx -> {
            System.out.println("WebSocket client disconnected");
        });

        ws.onError(ctx -> {
            System.out.println("WebSocket error: " + ctx.error());
        });
    }

    private void commandHelper(UserGameCommand command){
        if(command.getCommandType() == CONNECT)
        {
            String authToken;
            try{
                authToken = command.getAuthToken();
            }
            catch(Exception ex) {
                System.out.println(ex.getMessage());
                return;
            }

            String username;
            try {
                username = service.getUsername(authToken);
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
                return;
            }

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