package server;

import org.junit.jupiter.api.*;

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