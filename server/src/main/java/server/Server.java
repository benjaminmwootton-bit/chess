package server;

import dataaccess.MemoryDataAccess;
import dataaccess.SequelDataAccess;
import io.javalin.*;
import java.util.Map;
import com.google.gson.Gson;
import service.requestsandresults.*;
import service.Service;
import io.javalin.json.JavalinGson;

public class Server
{
    private final Javalin javalin;

    public Server()
    {
        Service service;
        try
        {
            service = new Service(new SequelDataAccess());
        }
        catch(Exception e)
        {
            throw new RuntimeException("", e);
        }

        javalin = Javalin.create(config ->
        {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });
        clearRoute(service);
        registerRoute(service);
        loginRoute(service);
        logOutRoute(service);
        listGamesRoute(service);
        createGameRoute(service);
        joinGameRoute(service);
    }

    private void clearRoute(Service service) {
        //Clear
        javalin.delete("/db", context ->
        {
            try {
                service.delete(new DeleteRequest());
                context.status(200);
                context.json(Map.of());
            } catch (Exception e) {
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

    private void registerRoute(Service service) {
        //Register
        javalin.post("/user", context ->
        {
            try {
                RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);
                RegisterResult result = service.register(request);
                context.status(200);
                context.json(result);
            } catch (BadRequestException e) {
                context.status(400);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (AlreadyTakenException e) {
                context.status(403);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });
    }

    private void loginRoute(Service service) {
        //Login
        javalin.post("/session", context ->
        {
            try {
                LogInRequest request = new Gson().fromJson(context.body(), LogInRequest.class);
                LogInResult result = service.logIn(request);
                context.status(200);
                context.json(result);
            } catch (BadRequestException e) {
                context.status(400);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (UnauthorizedException e) {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });
    }

    private void logOutRoute(Service service) {
        //LogOut
        javalin.delete("/session", context ->
        {
            try {
                String authToken = context.header("authorization");
                LogOutRequest request = new LogOutRequest(authToken);
                LogOutResult result = service.logOut(request);
                context.status(200);
                context.json(result);
            } catch (UnauthorizedException e) {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });
    }

    private void listGamesRoute(Service service) {
        //listGames
        javalin.get("/game", context ->
        {
            try {
                String authToken = context.header("authorization");
                ListGamesRequest request = new ListGamesRequest(authToken);
                ListGamesResult result = service.listGames(request);
                context.status(200);
                context.json(result);
            } catch (UnauthorizedException e) {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });
    }

    private void createGameRoute(Service service) {
        //createGame
        javalin.post("/game", context ->
        {
            try {
                String authToken = context.header("authorization");
                CreateGameRequest bodyRequest = new Gson().fromJson(context.body(), CreateGameRequest.class);
                CreateGameRequest request = new CreateGameRequest(authToken, bodyRequest.gameName());
                CreateGameResult result = service.createGame(request);
                context.status(200);
                context.json(result);
            } catch (UnauthorizedException e) {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (BadRequestException e) {
                context.status(400);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });
    }

    private void joinGameRoute(Service service) {
        //joinGame
        javalin.put("/game", context ->
        {
            try {
                String authToken = context.header("authorization");
                JoinGameRequest bodyRequest = new Gson().fromJson(context.body(), JoinGameRequest.class);
                JoinGameRequest request = new JoinGameRequest(authToken, bodyRequest.playerColor(), bodyRequest.gameID());
                JoinGameResult result = service.joinGame(request);
                context.status(200);
                context.json(result);
            } catch (BadRequestException e) {
                context.status(400);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (UnauthorizedException e) {
                context.status(401);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (AlreadyTakenException e) {
                context.status(403);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                context.status(500);
                context.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });
    }
}