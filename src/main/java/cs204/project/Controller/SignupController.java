package cs204.project.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import cs204.project.Entity.User;
import cs204.project.Service.UserDetailService;


@Controller
public class SignupController {

    @Autowired
    private UserDetailService userService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_new";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Collect all error messages
            String errorMessages = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining("<br>"));

            // Add the list of error messages to the model
            model.addAttribute("errorMessage", errorMessages);
            return "signup_new"; // Return to the signup page if there are validation errors
        }
        userService.registerUser(user);
        return "redirect:/login";
    }
}