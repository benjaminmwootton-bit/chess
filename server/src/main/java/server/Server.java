package server;

import dataaccess.MemoryDataAccess;
import io.javalin.*;
import java.util.Map;
import com.google.gson.Gson;
import service.requestsAndResults.*;
import service.service;
import io.javalin.json.JavalinGson;

public class Server
{
    private final Javalin javalin;

    public Server()
    {
        var service = new service(new MemoryDataAccess());

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
                e.printStackTrace();
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //Register
        javalin.post("/user", context ->
        {
            try
            {
                registerRequest request = new Gson().fromJson(context.body(), registerRequest.class);
                registerResult result = service.register(request);
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
                logInRequest request = new Gson().fromJson(context.body(), logInRequest.class);
                logInResult result = service.logIn(request);
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
                logOutRequest request = new logOutRequest(authToken);
                logOutResult result = service.logOut(request);
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
                listGamesRequest request = new listGamesRequest(authToken);
                listGamesResult result = service.listGames(request);
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
                createGameRequest bodyRequest = new Gson().fromJson(context.body(), createGameRequest.class);
                createGameRequest request = new createGameRequest(authToken, bodyRequest.gameName());
                createGameResult result = service.createGame(request);
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
                joinGameRequest request = new joinGameRequest(authToken, bodyRequest.color(), bodyRequest.gameID());
                joinGameResult result = service.joinGame(request);
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