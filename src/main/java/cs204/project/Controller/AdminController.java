package cs204.project.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
// import cs204.project.Entity.Player;

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

import cs204.project.Controller.Player;

import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/admin")
@SessionAttributes({ "playerGroups", "tournamentData" })
public class AdminController {

  @Autowired
  private UserDetailService userService;

  private RestTemplate restTemplate = new RestTemplate();

  // private List<List<Player>> playerGroups;

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

  @GetMapping("/startTournament/{id}")
  public String startTournament(@PathVariable Long id, Model model) {
    // API URL to get tournament details by ID
    String apiUrl = "http://localhost:8080/tournaments/" + id;

    // Use RestTemplate to call the API and fetch the tournament details
    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> tournamentData = restTemplate.getForObject(apiUrl, Map.class);

    // Retrieve player list from the tournament
    // List<Long> playerList = (List<Long>) tournamentData.get("playerList");
    int round = (int) tournamentData.get("round");
    List<?> playerListRaw = (List<?>) tournamentData.get("playerList");
    List<Long> playerList = new ArrayList<>();

    // Convert Integer to Long if necessary
    for (Object playerIdObj : playerListRaw) {
      if (playerIdObj instanceof Integer) {
        playerList.add(((Integer) playerIdObj).longValue()); // Convert Integer to Long
      } else {
        playerList.add((Long) playerIdObj); // Cast to Long directly

      }
    }
    // for mm api after its done
    // Create a list of maps for players
    List<Player> players = new ArrayList<>();
    for (int i = 0; i < playerList.size(); i++) {
      Long playerId = playerList.get(i);
      Player playerData = new Player(playerId, userService.findById(playerId).getRank());
      players.add(playerData);
    }

    // Prepare the payload for the matchmaking API
    Map<String, Object> payload = new HashMap<>();
    payload.put("tournamentId", id);
    payload.put("players", players);

    // API URL to send player list to matchmaking API
    String matchmakingApiUrl = "http://localhost:8080/matchmaking/first-round";

    // Call the matchmaking API to get the split lists of players
    // List<List<Player>> playerGroups = restTemplate.postForObject(matchmakingApiUrl, payload, List.class);

    // Call the matchmaking API and get a raw response
    List<Map<String, Object>> rawPlayerGroups = restTemplate.postForObject(matchmakingApiUrl, payload, List.class);

    // Manually map the response to List<List<Player>>
    List<List<Player>> playerGroups = new ArrayList<>();
    for (Map<String, Object> groupMap : rawPlayerGroups) {
      List<Player> group = new ArrayList<>();
      for (Map<String, Object> playerMap : (List<Map<String, Object>>) groupMap.get("players")) {
        Long playerId = ((Number) playerMap.get("id")).longValue();
        int rank = (int) playerMap.get("rank");
        Player player = new Player(playerId, rank);
        group.add(player);
      }
      playerGroups.add(group);
    }
    
    // Add player groups and tournament details to the model
    model.addAttribute("round", round);
    model.addAttribute("tournament", tournamentData);
    model.addAttribute("playerGroups", playerGroups);

    return "admin/startTournament"; // return the startTournament Thymeleaf view
  }

  @PostMapping("/startTournament/{id}")
  public String nextRound(@PathVariable Long id,
      @ModelAttribute("playerGroups") List<List<Player>> playerGroups,
      @ModelAttribute("tournamentData") Map<String, Object> tournamentData, Model model) {
    // public String nextRound(@PathVariable Long id, Model model) {
    // Handle the logic for starting the next round
    // API URL to get tournament details by ID
    String apiUrl = "http://localhost:8080/matchmaking/next-round";

    // Use RestTemplate to call the API and fetch the tournament details
    // RestTemplate restTemplate = new RestTemplate();
    // Map<String, Object> tournamentData = restTemplate.getForObject(apiUrl,
    // Map.class);

    // for testing
    // Retrieve player list from the tournament
    // List<Long> playerList = (List<Long>) tournamentData.get("playerList");
    int round = (int) tournamentData.get("round");

    // Construct the updated tournament data
    Map<String, Object> updatedTournament = new HashMap<>();
    updatedTournament.put("id", id);
    updatedTournament.put("name", tournamentData.get("name"));
    updatedTournament.put("date", tournamentData.get("date"));
    updatedTournament.put("rankRange", tournamentData.get("rankRange"));
    updatedTournament.put("status", tournamentData.get("status"));
    updatedTournament.put("region", tournamentData.get("region"));
    updatedTournament.put("playerList", tournamentData.get("playerList"));
    updatedTournament.put("round", ++round);

    // Send a PUT request to update the tournament
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
    restTemplate.put(tournamentApiUrl, updatedTournament);

    // List<List<Long>> playerGroups = new ArrayList<>();
    // for (int i = 0; i < 4; i++) {
    // playerGroups.add(playerList.subList(i * 8, Math.min((i + 1) * 8,
    // playerList.size())));
    // }

    // for mm api after its done
    // Prepare the payload for the matchmaking API
    Map<String, Object> payload = new HashMap<>();
    payload.put("tournamentId", id);
    payload.put("players", playerGroups);
    payload.put("round", round);

    // API URL to send player list to matchmaking API
    String matchmakingApiUrl = "http://localhost:8080/matchmaking";

    // Call the matchmaking API to get the split lists of players
    playerGroups = restTemplate.postForObject(matchmakingApiUrl, payload, List.class);

    model.addAttribute("round", round);
    model.addAttribute("tournament", tournamentData);
    model.addAttribute("playerGroups", playerGroups);

    return "admin/startTournament";
  }

}
