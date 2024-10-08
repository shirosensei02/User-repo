package cs204.project.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cs204.project.Entity.CustomUserDetails;


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

        if(authentication != null && authentication.isAuthenticated()){
            //Get Principal
            CustomUserDetails customUDetails = (CustomUserDetails)authentication.getPrincipal();

            //Get User ID
            System.out.println(customUDetails.getId());

            //Get User Role
            System.out.println(customUDetails.getAuthorities());
        }

        // Iterate over the user's roles (authorities)
        for
        (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                model.addAttribute("message", "Welcome Admin");
                return "redirect:/admin"; // Render a view that shows "Welcome Admin"
            }
        }

        // If not admin, assume the user has "USER" role
        model.addAttribute("message", "Welcome User");
        return "redirect:/user"; // Render a view that shows "Welcome User"
    }
}
