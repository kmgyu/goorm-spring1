package goorm.goormspring1.auth.exception;

public class UserNotFoundException extends RuntimeException {
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super("user not found");
        this.userId = userId;
    }

    public UserNotFoundException(String email) {
        super("user not found");
        this.userId = null;
    }

    public Long getUserId() {
        return userId;
    }
}
