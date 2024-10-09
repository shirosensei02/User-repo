package cs204.project.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

  @GetMapping("")
  public String getHomePage() {
    return "admin/admin_home";
  }

  @GetMapping("/tournaments")
  public String getTournaments(Model model) {
    RestTemplate restTemplate = new RestTemplate();

    // URL of the tournament service through gateway
    String tournamentApiUrl = "http://localhost:8080/tournaments";

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);
    System.out.println(tournaments.toString());

    // Pass the fetched tournaments to the Thymeleaf view
    model.addAttribute("admin_tournaments", tournaments);

    return "admin/admin_tournaments"; // This returns the admin_tournaments.html Thymeleaf view
  }
}
