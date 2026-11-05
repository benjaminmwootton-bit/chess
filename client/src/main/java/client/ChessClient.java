package client;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class ChessClient {
    public enum State {SIGNEDOUT, SIGNEDIN, QUIT}

    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ♙");
        System.out.println("Shall we play a game? Type Help to get started");
        Scanner scanner = new Scanner(System.in);
        State state = State.SIGNEDOUT;
        while (!state.equals(State.QUIT)) {
            String input = scanner.nextLine();
            if (state == State.SIGNEDOUT) {
                state = handlePreLogin(input);
            } else {
                state = handlePostLogin(input);
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
        } else {
            return improperInput(false);
        }
        return switch (cmd) {
            case "help" -> help(false);
            case "quit" -> quit();
            case "login" -> login(params);
            case "register" -> register(params);
            case "love" -> warGames(); //Love to. How about Global Thermonuclear War?
            default -> improperInput(false);
        };
    }

    private static State handlePostLogin(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd;
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (tokens.length > 0) {
            cmd = tokens[0];
        } else {
            return improperInput(true);
        }

        return switch (cmd) {
            case "help" -> help(true);
            case "logout" -> logout();
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe();
            default -> improperInput(true);
        };
    }

    private static State improperInput(boolean signedIn) {
        if (signedIn) {
            System.out.println("Enter Help, Logout, Create, List, Play, or Observe");
            return State.SIGNEDIN;
        } else {
            System.out.println("Enter Help, Quit, Login, or Register");
            return State.SIGNEDOUT;
        }
    }

    private static State help(boolean signedIn) {
        System.out.println("help - gives possible commands");
        if (signedIn) {
            System.out.println("logout - Logs you out");
            System.out.println("create <name> - Creates a new Chess Game");
            System.out.println("list - Lists all Chess Games");
            System.out.println("join <gameID> <white/black> - Join a Chess Game");
            System.out.println("observe <gameID> - Observe a Chess Game");
            return State.SIGNEDIN;
        } else {
            System.out.println("quit - Quits the Program");
            System.out.println("login <username> <password> - Logs into the Program");
            System.out.println("register <username> <password> <email> - Registers a New User");
            return State.SIGNEDOUT;
        }
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
        while (!city.equals("Utah"))
        {
            city = scanner.nextLine();//Las Vegas
        }
        return State.SIGNEDOUT;
    }
}
