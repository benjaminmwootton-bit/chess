package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import java.util.Map;
import com.google.gson.Gson;
import service.registerRequest;
import service.registerResult;
import service.logInRequest;
import service.logInResult;
import service.service;

public class Server
{
    private final Javalin javalin;
    private final DataAccess dataAccess;

    public Server()
    {
        dataAccess = new MemoryDataAccess();
        var service = new service();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        //Clear
        javalin.delete("/db", context ->
        {
            try
            {
                dataAccess.clear();
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
        // Register your endpoints and exception handlers here.
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