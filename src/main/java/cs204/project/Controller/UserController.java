package cs204.project.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

  @GetMapping("")
  public String getHomePage() {
    return "users/home_new";
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/tournaments")
  public String getTournaments(Model model) {
    RestTemplate restTemplate = new RestTemplate();

    // URL of the tournament service through gateway
    String tournamentApiUrl = "http://localhost:8080/tournaments";

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);
    // System.out.println(tournaments.toString());

    // Pass the fetched tournaments to the Thymeleaf view
    model.addAttribute("tournaments", tournaments);

    return "users/tournaments"; // This returns the tournaments.html Thymeleaf view
  }

  @GetMapping("/profile")
  public String getProfilePage() {
    return "";
  }

}
