package client;

import com.google.gson.Gson;
import service.requestsandresults.*;

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
        if(params.length != 3){
            System.out.println("To register enter your username, password, and email");
            return State.SIGNEDOUT;
        }
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        String json = gson.toJson(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                RegisterResult result = gson.fromJson(response.body(), RegisterResult.class);
                authToken = result.authToken();
                System.out.println("Registered and Logged In!");
                return State.SIGNEDIN;
            }
            if (response.statusCode() == 403) {
                System.out.println("Username already taken");
            } else {
                System.out.println("Registration failed: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
        return State.SIGNEDOUT;
    }

    public State logout() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                authToken = "";
                System.out.println("Logged Out");
                return State.SIGNEDOUT;
            }
            if (response.statusCode() == 401) {
                System.out.println("You are not authorized");
            } else {
                System.out.println("Logout Failed: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
        return State.SIGNEDIN;
    }

    public State create(String[] params) {
        if(params.length != 1){
            System.out.println("To create a game enter a game name");
            return State.SIGNEDIN;
        }
        CreateGameRequest request = new CreateGameRequest(authToken, params[0]);
        String json = gson.toJson(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Game Created!\nGameID: " + gson.fromJson(response.body(), CreateGameResult.class).gameID());
                return State.SIGNEDIN;
            }
            if (response.statusCode() == 401) {
                System.out.println("You are not authorized");
            } else {
                System.out.println("Create game failed: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
        return State.SIGNEDIN;
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

    public State clear() {
        return null;
    }
}