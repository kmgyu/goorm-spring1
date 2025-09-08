package goorm.goormspring1.handler;

import goorm.goormspring1.post.exception.PostNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 전역 예외 처리기
 * 모든 컨트롤러에서 발생하는 예외를 한 곳에서 처리
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    /**
     * PostNotFoundException 처리
     * 게시글을 찾을 수 없을 때 404 페이지로 이동
     */
    @ExceptionHandler(PostNotFoundException.class)
    public String handlePostNotFoundException(PostNotFoundException e, Model model) {
        logger.warn("Post not found: {}", e.getMessage());

        // 국제화 메시지 조회
        String errorMessage = messageSource.getMessage(
                "error.post.notfound",
                null,
                LocaleContextHolder.getLocale()
        );

        model.addAttribute("error", errorMessage);
        model.addAttribute("postId", e.getPostId());

        return "error/404";
    }





    /**
     * 기타 모든 예외 처리
     * 예상하지 못한 예외가 발생할 때 500 페이지로 이동
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        logger.error("Unexpected error occurred", e);

        // 국제화 메시지 조회
        String errorMessage = messageSource.getMessage(
                "error.server.internal",
                null,
                LocaleContextHolder.getLocale()
        );

        model.addAttribute("error", errorMessage);

        // 개발 환경에서는 상세 에러 정보 표시
        if (logger.isDebugEnabled()) {
            model.addAttribute("exception", e.getClass().getSimpleName());
            model.addAttribute("message", e.getMessage());
        }

        return "error/500";
    }
}