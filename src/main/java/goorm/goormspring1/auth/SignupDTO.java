package goorm.goormspring1.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class SignupDTO {

    @PostMapping("/login")
    public String login(LoginDto loginDto, HttpSession session) {
        User user = userService.authenticate(loginDto);

        // 세션에 사용자 정보 저장
        session.setAttribute("user", user);

        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 완전 삭제
        return "redirect:/";
    }

    @GetMapping("/admin")
    public String admin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        return "admin";
    }
}
