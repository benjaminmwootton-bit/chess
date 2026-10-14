package server;

import com.google.gson.Gson;
import server.Server.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import service.registerResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTests
{
    private Server server;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    void setup()
    {
        server = new Server();
        int port = server.run(0);
        baseUrl = "http://localhost:" + port;
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void shutDown()
    {
        server.stop();
    }

    @Test
    void registerUser() throws Exception
    {
        String body = """
                {"username": "Ben","password": "password123","email": "ben2@gmail.com"}""";
        HttpResponse<String> response = postUser(body);

        assertEquals(200, response.statusCode());

        var serializer = new Gson();
        var json = serializer.fromJson(response.body(), registerResult.class);

        assertEquals("Ben", json.username());
    }

    private HttpResponse<String> postUser(String body) throws Exception
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()
        );
    }
}