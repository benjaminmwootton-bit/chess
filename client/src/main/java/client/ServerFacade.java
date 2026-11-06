package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
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
        return State.SIGNEDOUT;
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
        ListGamesRequest request = new ListGamesRequest(authToken);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ListGamesResult result = gson.fromJson(response.body(), ListGamesResult.class);
                if(result.games() == null){
                    System.out.println("There are currently no games");
                    return State.SIGNEDIN;
                }
                int x = 1;
                for(GameData game : result.games()) {
                    System.out.print(x + ". " + game.gameName());
                    if(game.whiteUsername() != null){
                        System.out.print(". White Player: " + game.whiteUsername());
                    }
                    if(game.blackUsername() != null){
                        System.out.print(". Black Player: " + game.blackUsername());//#not racist
                    }
                    System.out.print("\n");
                    x++;
                }
                return State.SIGNEDIN;
            }
            if (response.statusCode() == 401) {
                System.out.println("You are not authorized");
            } else {
                System.out.println("List games failed: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
        return State.SIGNEDIN;
    }

    public State join(String[] params) {
        if(params.length != 2){
            System.out.println("To join a game enter white/black and a gameID.");
            return State.SIGNEDIN;
        }
        JoinGameRequest request;
        try{
            request = new JoinGameRequest(authToken, ChessGame.TeamColor.valueOf(params[0].toUpperCase()), Integer.parseInt(params[1]));}
        catch(Exception e){
            System.out.println("Invalid team color or gameID");
            return State.SIGNEDIN;
        }
        String json = gson.toJson(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Game joined!");
                return State.SIGNEDIN;
            }
            if (response.statusCode() == 401) {
                System.out.println("Invalid team color or gameID");
            }
            else if (response.statusCode() == 403) {
                System.out.println("Position already taken");
            }else {
                System.out.println("Login failed: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
        return State.SIGNEDOUT;
    }

    public State clear() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/db"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Deleted");
                return State.SIGNEDOUT;
            } else {
                System.out.println("Deletion failed: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
        return State.SIGNEDOUT;
    }
}