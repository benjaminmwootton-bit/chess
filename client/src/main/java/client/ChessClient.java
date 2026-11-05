package client;

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
                handlePreLogin(input);
            } else {
                handlePostLogin(input);
            }
        }
    }

    private static State handlePreLogin(String input)
    {
        return State.QUIT;
    }

    private static State handlePostLogin(String input)
    {
        return State.QUIT;
    }
}
