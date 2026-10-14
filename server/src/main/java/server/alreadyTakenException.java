package server;

public class alreadyTakenException extends RuntimeException
{
    public alreadyTakenException(String message) {
        super(message);
    }
}
