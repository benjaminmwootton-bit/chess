package server;

import com.google.gson.Gson;
import server.Server.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import service.registerResult;

public class ServerTests
{
    private Server server;

    @BeforeEach
    void setup()
    {
        server = new Server();
        int port = server.run(0);
    }

    @AfterEach
    void shutDown()
    {
        server.stop();
    }
}