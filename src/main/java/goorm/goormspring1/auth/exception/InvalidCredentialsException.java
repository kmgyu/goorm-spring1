package goorm.goormspring1.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super("Invalid Credential" + message);
    }
}
