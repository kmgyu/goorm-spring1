package goorm.goormspring1.security;

import goorm.goormspring1.post.Post;
import goorm.goormspring1.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PostRepository postRepository;

    // hasPermission(principal, domainObject, permission)
    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (auth == null || targetDomainObject == null || permission == null) return false;

        String perm = String.valueOf(permission).toLowerCase();

        if (targetDomainObject instanceof Post post) {
            return checkPost(auth, post, perm);
        }
        return false;
    }

    // hasPermission(principal, targetId, targetType, permission)
    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        if (auth == null || targetId == null || targetType == null || permission == null) return false;

        String type = targetType.toLowerCase();
        String perm = String.valueOf(permission).toLowerCase();

        if (!"post".equals(type)) return false;

        Long id;
        try {
            id = (targetId instanceof Long) ? (Long) targetId : Long.valueOf(targetId.toString());
        } catch (NumberFormatException e) {
            return false;
        }

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) return false;

        return checkPost(auth, post, perm);
    }

    private boolean checkPost(Authentication auth, Post post, String perm) {
        // 현재 사용자 식별 (커스텀 Principal이면 그에 맞게 추출)
        String currentEmail = auth.getName();

        // 작성자/이메일 널 방어
        String authorEmail = (post.getAuthor() != null) ? post.getAuthor().getEmail() : null;

        // READ는 모두 허용, WRITE/DELETE는 본인만
        return switch (perm) {
            case "read" -> true;
            case "write", "delete" -> authorEmail != null && authorEmail.equals(currentEmail);
            default -> false;
        };
    }
}
