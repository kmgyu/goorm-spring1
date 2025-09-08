package goorm.goormspring1.auth.exception;

public class DuplicateDeniedException extends RuntimeException {
    public DuplicateDeniedException(String message) {
        super( "Email Duplicate Denied" + message);
    }
}
