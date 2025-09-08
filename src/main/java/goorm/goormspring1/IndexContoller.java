package goorm.goormspring1;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexContoller {
    @GetMapping
    public String index(Model model) {
        return "redirect:/posts";
    }
}
