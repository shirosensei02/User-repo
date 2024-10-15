package cs204.project.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cs204.project.Entity.User;
import cs204.project.Service.UserDetailService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.ui.Model;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private UserDetailService userService;

  private RestTemplate restTemplate = new RestTemplate();

  @GetMapping("")
  public String getDashboard() {
    return "admin/admin-dashboard";
  }

  @GetMapping("/admin-tournaments")
  public String getTournaments(Model model) {
    // RestTemplate restTemplate = new RestTemplate();

    // URL of the tournament service through gateway
    String tournamentApiUrl = "http://localhost:8080/tournaments";

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);
    tournaments.sort(Comparator.comparing(t -> (String) t.get("date"))); // Adjust according to your date format

    // Pass the fetched tournaments to the Thymeleaf view
    model.addAttribute("adminTournaments", tournaments);

    return "admin/admin-tournaments"; // This returns the tournaments.html Thymeleaf view
  }

  @GetMapping("/addTournament")
  public String showAddTournamentPage(Model model) {
    return "admin/addTournament";
  }

  @PostMapping("/addTournament")
  public String postMethodName(
      @RequestParam String name,
      @RequestParam String datetime,
      @RequestParam int minRank,
      @RequestParam int maxRank,
      @RequestParam(defaultValue = "Open") String status,
      @RequestParam String region) {

    // System.out.println("Tournament Name: " + name);
    // System.out.println("DateTime: " + datetime);
    // System.out.println("Min Rank: " + minRank);
    // System.out.println("Max Rank: " + maxRank);
    // System.out.println("Status: " + status);
    // System.out.println("Region: " + region);

    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // LocalDate date = LocalDate.parse(datetime.substring(0, 10), formatter);

    // Create a map for the tournament data
    Map<String, Object> tournamentData = new HashMap<>();
    tournamentData.put("name", name);
    tournamentData.put("date", datetime);
    tournamentData.put("rankRange", new int[] { minRank, maxRank });
    tournamentData.put("status", status);
    tournamentData.put("region", region);

    // Create headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // Create a request entity (tournament data with headers)
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(tournamentData, headers);

    // URL of the Tournament API
    String tournamentApiUrl = "http://localhost:8080/tournaments";
    // RestTemplate restTemplate = new RestTemplate();
    // Send POST request to the Tournament API
    try {
      restTemplate.exchange(tournamentApiUrl, HttpMethod.POST, request, String.class);
      System.out.println("Tournament added successfully!");
    } catch (Exception e) {
      System.err.println("Error while adding tournament: " + e.getMessage());
    }

    return "redirect:/admin/admin-tournaments";
  }

  @GetMapping("/user-management")
  public String getUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size,
      Model model) {
    Pageable pageable = PageRequest.of(page, size);
    Page<User> userPage = userService.findAllUsers(pageable);

    model.addAttribute("users", userPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", userPage.getTotalPages());

    return "admin/user-management";
  }

  @GetMapping("/updateTournament/{id}")
  public String getUpdateTournament(@PathVariable("id") Long id, Model model) {
    // RestTemplate restTemplate = new RestTemplate();

    // Fetch the tournament data by ID (modify API call as needed)
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
    Map<String, Object> tournament = restTemplate.getForObject(tournamentApiUrl, Map.class);

    // Pass the tournament data to the Thymeleaf view
    model.addAttribute("tournament", tournament);

    return "admin/updateTournament"; // Returns the update form page
  }

  // Handle the form submission for updating the tournament
  @PostMapping("/updateTournament")
  public String updateTournament(
      @RequestParam Long id,
      @RequestParam String name,
      @RequestParam String datetime,
      @RequestParam int minRank,
      @RequestParam int maxRank,
      @RequestParam(defaultValue = "Open") String status,
      @RequestParam String region) {

    // Construct the updated tournament data
    Map<String, Object> updatedTournament = new HashMap<>();
    updatedTournament.put("id", id);
    updatedTournament.put("name", name);
    updatedTournament.put("date", datetime);
    updatedTournament.put("rankRange", new int[] { minRank, maxRank });
    updatedTournament.put("status", status);
    updatedTournament.put("region", region);

    // Send a PUT request to update the tournament
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
    restTemplate.put(tournamentApiUrl, updatedTournament);

    // Redirect back to the tournament list after updating
    return "redirect:/admin/admin-tournaments";
  }

  @DeleteMapping("/tournaments/{id}")
  public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
    // URL of the tournament service through gateway
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;

    // Call the tournament API to delete the tournament
    restTemplate.delete(tournamentApiUrl);

    return ResponseEntity.noContent().build(); // Respond with 204 No Content
  }

  // @GetMapping("startTournament/{id}")
  // public String startTournament(){
    
  // }

}
