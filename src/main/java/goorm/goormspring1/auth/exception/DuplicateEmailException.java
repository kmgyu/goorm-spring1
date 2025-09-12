package goorm.goormspring1.auth.exception;

public class DuplicateEmailException extends RuntimeException {
    private final String email;
    public DuplicateEmailException(String email) {
        super("duplicate email:" + email);
        this.email = email;
    }

    public String getEmail() {return this.email;}
}
