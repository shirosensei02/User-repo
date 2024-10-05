package cs204.project.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {

    @GetMapping("/user/home")
    public String userHome(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "User/index"; // Return the User home page (view name: user/home.html)
    }

    @GetMapping("/user/login")
    public String login() {
        return "User/login"; // Correct path to the Thymeleaf template
    }

    @GetMapping("/user/signup")
    public String signup() {
        return "User/signup"; // Correct path to the Thymeleaf template
    }

    @GetMapping("/admin/home")
    public String adminHome(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "Admin/index"; // Return the Admin home page (view name: admin/home.html)
    }

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "Admin/login"; // Points to the admin login page (admin/login.html)
    }
}
