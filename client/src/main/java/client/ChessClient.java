package client;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient
{
    public enum State {SIGNEDOUT, SIGNEDIN, QUIT}

    public static void main(String[] args)
    {
        System.out.println("♕ 240 Chess Client: ♙");
        Scanner scanner = new Scanner(System.in);
        State state = State.SIGNEDOUT;
        while (!state.equals(State.QUIT))
        {
            String input = scanner.nextLine();
            if (state == State.SIGNEDOUT) {
                state = handlePreLogin(input);
            } else {
                state = handlePostLogin(input);
            }
        }
    }

    private static State handlePreLogin(String input)
    {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd;
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if(tokens.length > 0){
            cmd = tokens[0];}
        else {
            return improperInput(false);}
        return switch (cmd)
        {
            case "help" -> help();
            case "quit" -> quit();
            case "login" -> login(params);
            case "register" -> register(params);
            default -> improperInput(false);
        };
    }

    private static State handlePostLogin(String input)
    {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd;
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if(tokens.length > 0){
            cmd = tokens[0];}
        else {
            return improperInput(true);}

        return switch (cmd) {
            case "help" -> help();
            case "logout" -> logout();
            case "create game" -> createGame(params);
            case "List games" -> listGames();
            case "play game" -> playGame(params);
            case "observe game" -> observeGame();
            default -> improperInput(true);
        };
    }

    private static State improperInput(boolean SignedIn)
    {
        if(SignedIn) {
            System.out.println("Enter Help, Logout, Create Game, List Games, Play Game, or Observe Game");
            return State.SIGNEDIN;
        }
        else {
            System.out.println("Enter Help, Quit, Login, or Register");
            return State.SIGNEDOUT;
        }
    }
}
