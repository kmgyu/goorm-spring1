package goorm.goormspring1.auth.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super("duplicate email" + message);
    }
}
