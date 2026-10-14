package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import java.util.Map;
import com.google.gson.Gson;
import service.registerRequest;
import service.registerResult;
import service.service;

public class Server
{
    private final Javalin javalin;
    private final DataAccess dataAccess;

    public Server()
    {
        dataAccess = new MemoryDataAccess();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

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

        javalin.post("/user", context ->
        {
            try
            {
                registerRequest request = new Gson().fromJson(context.body(), registerRequest.class);
                var service = new service();
                registerResult result = service.register(request);
                context.status(200);
                context.json(Map.of("username",""));
            }
            catch (Exception e)
            {
                context.status(400);
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