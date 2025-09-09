package goorm.goormspring1.security;

import goorm.goormspring1.post.Post;
import goorm.goormspring1.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final PostRepository postRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(permission instanceof String)) {
            return false;
        }

        String permissionString = (String) permission;

        if (targetDomainObject instanceof Post) {
            Post post = (Post) targetDomainObject;
            return hasPostPermission(authentication, post, permissionString);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null) {
            return false;
        }

        if ("Post".equals(targetType) && "WRITE".equals(permission)) {
            return hasPostWritePermission(authentication, (Long) targetId);
        }
        return false;
    }

    private boolean hasPostPermission(Authentication authentication, Post post, String permission) {
        String userEmail = authentication.getName();

        switch (permission) {
            case "WRITE":
            case "DELETE":
                return post.getAuthor().getEmail().equals(userEmail);
            case "READ":
                return true; // 모든 사용자가 읽기 가능
            default:
                return false;
        }
    }

    private boolean hasPostWritePermission(Authentication authentication, Long postId) {
        try {
            Post post = postRepository.findById(postId).orElse(null);
            if (post == null) {
                return false;
            }

            String userEmail = authentication.getName();
            return post.getAuthor().getEmail().equals(userEmail);
        } catch (Exception e) {
            return false;
        }
    }
}
