package service.server;

import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

public class ServerTests
{
    private Server server;

    @BeforeEach
    void setup()
    {
        server = new Server();
        int port = server.run(0);
        var testUser = createTestUser();
    }

    @AfterEach
    void shutDown()
    {
        server.stop();
    }


    public UserData createTestUser()
    {
        return new UserData("Ben", "password123", "ben2@gmail.com");
    }

}