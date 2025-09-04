package goorm.goormspring1.post.exception;

public class PostNotFoundException extends RuntimeException{

    private final Long postId;

    public PostNotFoundException(Long postId) {
        super("Post not found with ID" + postId);
        this.postId = postId;
    }

    public PostNotFoundException(String message) {
        super(message);
        this.postId = null;
    }

    public Long getPostId() {
        return postId;
    }
}
