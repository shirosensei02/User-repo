package cs204.project.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("")
    public String getHomePage(){
        return "home_new";
    }

    @GetMapping("/tournaments")
    public String getTournaments(){
        return "tournaments";
    }

    @GetMapping("/profile")
    public String getProfilePage(){
        return "";
    }
}
