package goorm.goormspring1.post;

import goorm.goormspring1.auth.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // 메인 페이지
//    @GetMapping("/")
//    public String home() {
//        return "index";
//    }

    // 게시글 목록 (기본)
    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "seq", direction = Sort.Direction.DESC)
                       Pageable pageable, Model model) {
        Page<Post> posts = postService.findAll(pageable);
        model.addAttribute("posts", posts);
        return "post/list";
    }

    // 게시글 상세 조회
    @GetMapping("/{seq}")
    public String show(@PathVariable Long seq, Model model) {
        Post post = postService.findBySeq(seq);
        model.addAttribute("post", post);
        return "post/detail";
    }

    // 게시글 작성 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("post", new Post());
        return "post/write";
    }

    // 게시글 저장 → 목록으로
    @PostMapping
    public String create(@Valid @ModelAttribute("post") Post post,
                         BindingResult bindingResult,
//                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        // 인터셉터 등록 시 뚫릴까?
//        User user = (User) session.getAttribute("user");

        if (bindingResult.hasErrors()) {
            return "post/write";
        }

        postService.save(post);
        redirectAttributes.addFlashAttribute("message", "flash.post.updated");
        return "redirect:/posts";
    }

    // 게시글 수정 폼
    @GetMapping("/{seq}/edit")
    public String editForm(@PathVariable Long seq, Model model) {
        Post post = postService.findBySeq(seq);
        model.addAttribute("post", post);
        return "post/write";
    }

    // 게시글 수정 → 상세보기로
    @PutMapping("/{seq}")
    public String update(@PathVariable Long seq,
                         @Valid @ModelAttribute("post") Post post,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            post.setSeq(seq);
            return "post/write";
        }
        postService.update(seq, post);
        redirectAttributes.addFlashAttribute("message", "flash.post.created");
        return "redirect:/posts/" + seq;
    }

    // 게시글 삭제 → 목록으로
    @PostMapping("/{seq}/delete")
    public String delete(@PathVariable Long seq,
                         RedirectAttributes redirectAttributes) {
        postService.delete(seq);
        redirectAttributes.addFlashAttribute("message", "flash.post.deleted");
        return "redirect:/posts";

    }
}