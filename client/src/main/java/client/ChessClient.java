package client;

import client.websocket.NotificationHandler;
import exception.ResponseException;

import client.websocket.WebSocketFacade;
import server.Server;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class ChessClient implements NotificationHandler{

    private final ServerFacade server;
    private static WebSocketFacade ws;
    private static final ServerFacade FACADE = new ServerFacade("http://localhost:8080");
    private static int gameID;

    public ChessClient(String serverUrl) throws ResponseException{
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public static void main(String[] args) throws ResponseException, IOException {
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Client: ♙");
        System.out.println("Shall we play a game? Type Help to get started");
        Scanner scanner = new Scanner(System.in);

        State state = State.SIGNEDOUT;
        while (!state.equals(State.QUIT)) {
            String input = scanner.nextLine();
            if (state == State.SIGNEDOUT) {
                state = handlePreLogin(input);
            }
            else if (state == State.SIGNEDIN){
                state = handlePostLogin(input);
            }
            else if(state == State.INGAME){
                state = handleInGame(input);
            }
        }
        System.out.println("Goodbye :)");
    }

    private static State handlePreLogin(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd;
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (tokens.length > 0) {
            cmd = tokens[0];
        }
        else {
            return improperInput(State.SIGNEDOUT);
        }
        return switch (cmd) {
            case "help" -> help(State.SIGNEDOUT);
            case "quit", "exit" -> quit();
            case "login" -> FACADE.login(params);
            case "register" -> FACADE.register(params);
            case "love" -> warGames(); //Love to. How about Global Thermonuclear War?
            default -> improperInput(State.SIGNEDOUT);
        };
    }

    private State handlePostLogin(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd;
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (tokens.length > 0) {
            cmd = tokens[0];
        }
        else {
            return improperInput(State.SIGNEDIN);
        }
        if(cmd.equals("join")){
            this.gameID = Integer.parseInt(params[1]);
        }
        return switch (cmd) {
            case "help" -> help(State.SIGNEDIN);
            case "logout" -> FACADE.logout();
            case "create" -> FACADE.create(params);
            case "list" -> FACADE.list();
            case "join" -> FACADE.join(params);
            case "observe" -> FACADE.observe(params);
            case "clear" -> FACADE.clear();
            default -> improperInput(State.SIGNEDIN);
        };
    }

    private static State handleInGame(String input) throws ResponseException {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd;
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (tokens.length > 0) {
            cmd = tokens[0];
        }
        else{
            return improperInput(State.INGAME);
        }

        String authToken = FACADE.getAuthToken();

        return switch (cmd){
            case "help" -> help(State.INGAME);
            case "redraw" -> ws.redraw(authToken, gameID);
            case "leave" -> ws.leave(authToken, gameID);
            case "move" -> ws.move(authToken, gameID, params[1]);
            case "resign" -> ws.resign(authToken, gameID);
            case "highlight" -> ws.highlight(authToken, gameID, params[1]);
            default -> improperInput(State.INGAME);
        };
    }

    private static State improperInput(State state) {
        if (state == State.SIGNEDOUT) {
            System.out.println("Enter Help, Quit, Login, or Register");
            return State.SIGNEDOUT;

        }
        else if(state == State.SIGNEDIN){
            System.out.println("Enter Help, Logout, Create, List, Play, or Observe");
            return State.SIGNEDIN;
        }
        else if(state == State.INGAME){
            System.out.println("Enter Help, Redraw, Leave, Move, Resign, or Highlight");
            return State.INGAME;
        }
        else{
            return State.QUIT;
        }
    }

    private static State help(State state) {
        System.out.println("help - gives possible commands");
        if (state == State.SIGNEDOUT) {
            System.out.println("quit - Quits the Program");
            System.out.println("login <username> <password> - Logs into the Program");
            System.out.println("register <username> <password> <email> - Registers a New User");
            return State.SIGNEDOUT;
        }
        else if (state == State.SIGNEDIN){
            System.out.println("Help - gives possible commands");
            System.out.println("logout - Logs you out");
            System.out.println("create <name> - Creates a new Chess Game");
            System.out.println("list - Lists all Chess Games");
            System.out.println("join <white/black> <gameID>  - Join a Chess Game");
            System.out.println("observe <gameID> - Observe a Chess Game");
            return State.SIGNEDIN;
        }
        else if(state == State.INGAME){
            System.out.println("help - gives possible commands");
            System.out.println("redraw - Redraws the Chess Board");
            System.out.println("leave - Leaves the game without resigning");
            System.out.println("move <chess move> - Make a chess move. Ex:e2e4");
            System.out.println("resign - Resign the game");
            System.out.println("highlight <square> - Highlights all possible moves for a piece. Ex:e2");
            return State.INGAME;
        }
        else{
            return State.QUIT;}
    }

    private static State quit()
    {
        return State.QUIT;
    }

    private static State warGames()
    {
        System.out.println("Wouldn't you prefer a good game of chess?");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();//Later. Let's play Global Thermonuclear War.
        System.out.println("Fine.\n Which side do you want?");
        System.out.println("1. United States\n 2. Soviet Union\n Please Choose One:");
        scanner.nextLine();
        System.out.println("Awaiting first strike command");
        System.out.println("Please list primary targets by city/or country name:");
        String city = "";
        while (!city.equals("Utah")) {
            city = scanner.nextLine();//Las Vegas
        }
        return State.SIGNEDOUT;
    }

    @Override
    public void notify(ServerMessage message) {
        ServerMessage.ServerMessageType messageType = message.getServerMessageType();
        if(messageType == LOAD_GAME){
            System.out.println("Game Loaded");}
        else if (messageType == NOTIFICATION){
            System.out.println(message.getMessage());}
        else if(messageType == ERROR){
            System.out.println("Error: " + message.getMessage());}
    }
}