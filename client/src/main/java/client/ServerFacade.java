package client;

import com.google.gson.Gson;
import server.Server;
import service.requestsandresults.RegisterRequest;
import service.requestsandresults.LogInRequest;
import service.requestsandresults.LogInResult;
import service.requestsandresults.RegisterRequest;
import service.requestsandresults.RegisterResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade
{
    private final HttpClient client;
    private final Gson gson;
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String serverUrl) {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.serverUrl = serverUrl;
        this.authToken = null;
    }

    public State login(String[] params) {
        if(params.length != 2){
            System.out.println("To login enter your username and password");
            return State.SIGNEDOUT;
        }
        LogInRequest request = new LogInRequest(params[0], params[1]);
        String json = gson.toJson(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                LogInResult result = gson.fromJson(response.body(), LogInResult.class);
                authToken = result.authToken();
                System.out.println("Logged in!");
                return State.SIGNEDIN;
            }
            if (response.statusCode() == 401) {
                System.out.println("Invalid username or password");
            } else {
                System.out.println("Login failed: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
        return State.SIGNEDOUT;
    }

    public State register(String[] params) {
        return null;
    }

    public State logout() {
        return null;
    }

    public State create(String[] params) {
        return null;
    }

    public State list() {
        return null;
    }

    public State join(String[] params) {
        return null;
    }

    public State observe(String[] params) {
        return null;
    }
}