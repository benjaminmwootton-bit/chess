package server;

import dataaccess.MemoryDataAccess;
import io.javalin.*;
import java.util.Map;
import com.google.gson.Gson;
import Service.requestsAndResults.*;
import Service.Service;
import io.javalin.json.JavalinGson;

public class Server
{
    private final Javalin javalin;

    public Server()
    {
        var service = new Service(new MemoryDataAccess());

        javalin = Javalin.create(config ->
        {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        //Clear
        javalin.delete("/db", context ->
        {
            try
            {
                service.delete();
                context.status(200);
                context.json(Map.of());
            }
            catch (Exception e)
            {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //Register
        javalin.post("/user", context ->
        {
            try
            {
                RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);
                RegisterResult result = service.register(request);
                context.status(200);
                context.json(result);
            }
            catch (badRequestException e)
            {
                context.status(400);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (alreadyTakenException e)
            {
                context.status(403);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (Exception e)
            {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //Login
        javalin.post("/session", context ->
        {
            try
            {
                LogInRequest request = new Gson().fromJson(context.body(), LogInRequest.class);
                LogInResult result = service.logIn(request);
                context.status(200);
                context.json(result);
            }
            catch (badRequestException e)
            {
                context.status(400);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (unauthorizedException e)
            {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (Exception e)
            {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //LogOut
        javalin.delete("/session", context ->
        {
            try
            {
                String authToken = context.header("authorization");
                LogOutRequest request = new LogOutRequest(authToken);
                LogOutResult result = service.logOut(request);
                context.status(200);
                context.json(result);
            }
            catch (unauthorizedException e)
            {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (Exception e)
            {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //listGames
        javalin.get("/game", context ->
        {
            try
            {
                String authToken = context.header("authorization");
                ListGamesRequest request = new ListGamesRequest(authToken);
                ListGamesResult result = service.listGames(request);
                context.status(200);
                context.json(result);
            }
            catch (unauthorizedException e)
            {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (Exception e)
            {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //createGame
        javalin.post("/game", context ->
        {
            try
            {
                String authToken = context.header("authorization");
                CreateGameRequest bodyRequest = new Gson().fromJson(context.body(), CreateGameRequest.class);
                CreateGameRequest request = new CreateGameRequest(authToken, bodyRequest.gameName());
                CreateGameResult result = service.createGame(request);
                context.status(200);
                context.json(result);
            }
            catch (badRequestException e)
            {
                context.status(400);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (unauthorizedException e)
            {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (Exception e)
            {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //joinGame
        javalin.put("/game", context ->
        {
            try
            {
                String authToken = context.header("authorization");
                joinGameRequest bodyRequest = new Gson().fromJson(context.body(), joinGameRequest.class);
                joinGameRequest request = new joinGameRequest(authToken, bodyRequest.playerColor(), bodyRequest.gameID());
                JoinGameResult result = service.joinGame(request);
                context.status(200);
                context.json(result);
            }
            catch (badRequestException e)
            {
                context.status(400);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (unauthorizedException e)
            {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (alreadyTakenException e)
            {
                context.status(403);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
            catch (Exception e)
            {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });


    }

    public int run(int desiredPort)
    {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}