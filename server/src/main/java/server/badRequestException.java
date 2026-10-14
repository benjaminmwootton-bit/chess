package server;

public class badRequestException extends RuntimeException
{
    public badRequestException(String message) {
        super(message);
    }
}
