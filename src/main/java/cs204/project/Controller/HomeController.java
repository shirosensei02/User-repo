package cs204.project.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HomeController { 

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/")
    public String home(Model model) {
        // Get the authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Iterate over the user's roles (authorities)
        for
        (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                model.addAttribute("message", "Welcome Admin");
                return "welcome"; // Render a view that shows "Welcome Admin"
            }
        }

        // If not admin, assume the user has "USER" role
        model.addAttribute("message", "Welcome User");
        return "welcome"; // Render a view that shows "Welcome User"
    }
}
