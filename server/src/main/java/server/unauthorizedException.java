package server;

public class unauthorizedException extends RuntimeException
{
    public unauthorizedException(String message) {
        super(message);
    }
}