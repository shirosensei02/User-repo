package cs204.project.Controller;

import org.json.HTTP;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import cs204.project.Entity.CustomUserDetails;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {




  @GetMapping("")
  public String getHomePage(Model model) {
    RestTemplate restTemplate = new RestTemplate();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUDetails = (CustomUserDetails) authentication.getPrincipal();
    Long userid = customUDetails.getId();

    // URL of the tournament service through gateway
    String tournamentApiUrl = "http://localhost:8080/tournaments/player/" + userid;

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);

    model.addAttribute("tournaments", tournaments);

    return "users/home_new";
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/tournaments")
  public String getTournaments(Model model) {
    RestTemplate restTemplate = new RestTemplate();

    // URL of the tournament service through gateway
    String tournamentApiUrl = "http://localhost:8080/tournaments/available";

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);
    // System.out.println(tournaments.toString());

    // Pass the fetched tournaments to the Thymeleaf view
    model.addAttribute("tournaments", tournaments);

    return "users/tournaments"; // This returns the tournaments.html Thymeleaf view
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/tournaments/join")
  public String joinTournaments(@RequestParam("id") Long tournamentId) {
    RestTemplate restTemplate = new RestTemplate();
    System.out.println("Called");
    System.out.println("tournamentid: " + tournamentId);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUDetails = (CustomUserDetails) authentication.getPrincipal();
    Long userid = customUDetails.getId();

    // URL of the tournament service through gateway
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + tournamentId + "/player/" + userid;

    // Post request
    restTemplate.postForObject(tournamentApiUrl, null, Void.class);

    return "redirect:/user"; // This returns the tournaments.html Thymeleaf view
  }

  @GetMapping("/profile")
  public String getProfilePage() {
    return "";
  }
}
