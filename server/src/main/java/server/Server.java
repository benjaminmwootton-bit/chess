package server;

import io.javalin.*;
import io.javalin.http.Context;
import java.util.Map;
import com.google.gson.Gson;

public class    Server
{

    private final Javalin javalin;

    public Server()
    {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", ctx -> ctx.json(Map.of()));
        // Register your endpoints and exception handlers here.
    }

    private static void handle(Context ctx)
    {
        var r = Map.of();
        var json = new Gson();
        ctx.json(json.toJson(r));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
