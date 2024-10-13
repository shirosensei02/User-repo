package cs204.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

// import cs204.project.Entity.User;
// import cs204.project.Service.UserDetailService;
import cs204.project.model.player.*;


@Controller
public class SignupController {

    @Autowired
    private PlayerService players;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new Player());
        return "signup_new";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute Player player) {
        players.registerUser(player);
        return "redirect:/login";
    }

    // @PostMapping(value = "/user/signup", consumes = "application/json")
//     public Player createUser(@RequestBody Player user) {
//         user.setPassword(passwordEncoder.encode(user.getPassword()));
//         return players.createPlayer(user);
//     }
}