package goorm.goormspring1.auth;

import goorm.goormspring1.auth.dto.LoginDTO;
import goorm.goormspring1.auth.dto.ProfileUpdateDTO;
import goorm.goormspring1.auth.dto.SignupDTO;
import goorm.goormspring1.auth.exception.AccessDeniedException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final MessageSource messageSource;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupDTO", new SignupDTO());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupDTO signupDTO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Locale locale) {

        if (result.hasErrors()) {
            return "auth/signup";
        }

        try {
            User user = userService.signup(signupDTO);
            String message = messageSource.getMessage("flash.user.created", null, locale);
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/";
        } catch (Exception e) {
            result.reject("signup.failed", e.getMessage());
            return "auth/signup";
        }
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO,
                        BindingResult result,
                        HttpSession session,
                        RedirectAttributes redirectAttributes,
                        Locale locale) {

        if (result.hasErrors()) {
            return "auth/login";
        }

        try {
            User user = userService.authenticate(loginDTO);

            // 세션에 사용자 정보 저장
            session.setAttribute("user", user);

            String message = messageSource.getMessage("flash.login.success", null, "로그인되었습니다.", locale);
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/";
        } catch (Exception e) {
            result.reject("login.failed", e.getMessage());
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session,
                         RedirectAttributes redirectAttributes,
                         Locale locale) {

        session.invalidate();

        String message = messageSource.getMessage("flash.logout.success", null, "로그아웃되었습니다.", locale);
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profileForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // 최신 사용자 정보 조회
        User currentUser = userService.findById(user.getId());

        // 프로필 DTO 생성 및 기본값 설정
        ProfileUpdateDTO profileUpdateDto = new ProfileUpdateDTO();
        profileUpdateDto.setNickname(currentUser.getNickname());

        model.addAttribute("profileUpdateDto", profileUpdateDto);
        model.addAttribute("currentUser", currentUser);

        return "auth/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute ProfileUpdateDTO profileUpdateDto,
                                BindingResult result,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                Model model,
                                Locale locale) {

        User user = (User) session.getAttribute("user");

        if (result.hasErrors()) {
            User currentUser = userService.findById(user.getId());
            model.addAttribute("currentUser", currentUser);
            return "auth/profile";
        }

        try {
            User updatedUser = userService.updateProfile(user.getId(), profileUpdateDto);

            // 세션의 사용자 정보 업데이트
            session.setAttribute("user", updatedUser);

            String message = messageSource.getMessage("flash.profile.updated", null, "프로필이 수정되었습니다.", locale);
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/auth/profile";
        } catch (Exception e) {
            result.reject("profile.update.failed", e.getMessage());
            User currentUser = userService.findById(user.getId());
            model.addAttribute("currentUser", currentUser);
            return "auth/profile";
        }
    }

//    @GetMapping("/admin")
//    public String admin(HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        if (user == null || !user.isAdmin()) {
//            throw new AccessDeniedException("관리자 권한이 필요합니다.");
//        }
//        return "admin";
//    }
}
