package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import java.util.Map;
import com.google.gson.Gson;

public class    Server
{

    private final Javalin javalin;
    private final DataAccess dataAccess;

    public Server()
    {
        dataAccess = new MemoryDataAccess();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", ctx ->
        {
            try
            {
                dataAccess.clear();
                ctx.status(200);
                ctx.json(Map.of());
            }
            catch (Exception e)
            {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: " + e.getMessage()));
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