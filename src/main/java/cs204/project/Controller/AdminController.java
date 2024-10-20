package cs204.project.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cs204.project.Entity.User;
import cs204.project.Service.AdminService;
import cs204.project.Service.UserDetailService;
// import cs204.project.Entity.Player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

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
import cs204.project.Service.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private UserDetailService userService;

  @Autowired
  private AdminService adminService;

  private RestTemplate restTemplate = new RestTemplate();

  @GetMapping("")
  public String getDashboard() {
    return "redirect:/admin/admin-tournaments";
  }

  @SuppressWarnings("unchecked")
  @GetMapping("/admin-tournaments")
  public String getTournaments(Model model) {

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = adminService.getAllTournaments();
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
  public String addTournament(
      @RequestParam String name,
      @RequestParam String datetime,
      @RequestParam int minRank,
      @RequestParam int maxRank,
      @RequestParam(defaultValue = "Open") String status,
      @RequestParam String region) {

    // Create a map for the tournament data
    Map<String, Object> tournamentData = new HashMap<>();
    tournamentData.put("name", name);
    tournamentData.put("date", datetime);
    tournamentData.put("rankRange", new int[] { minRank, maxRank });
    tournamentData.put("status", status);
    tournamentData.put("region", region);

    try {
      adminService.addTournament(tournamentData);
    } catch (Exception e) {
      System.err.println("Error while adding tournament: " + e.getMessage());
    }

    return "redirect:/admin/admin-tournaments";
  }

  @GetMapping("/user-management")
  public String getUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size,
      @RequestParam(required = false) String username,
      Model model) {

    Pageable pageable = PageRequest.of(page, size);
    Page<User> userPage;

    if (username != null && !username.isEmpty()) {
      userPage = userService.searchByUsername(username, pageable); // Modify your service method to search
    } else {
      userPage = userService.findAllUsers(pageable);
    }

    model.addAttribute("users", userPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", userPage.getTotalPages());
    model.addAttribute("username", username); // Add the search term to the model

    return "admin/user-management";
  }

  @GetMapping("/updateTournament/{id}")
  public String getUpdateTournament(@PathVariable("id") Long id, Model model) {

    try {
      Map<String, Object> tournament = adminService.getTournamentById(id);
      // Pass the tournament data to the Thymeleaf view
      model.addAttribute("tournament", tournament);
    } catch (Exception e) {
      // return error page
      System.out.println(e.getMessage());
      // return "error";
    }

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

    try {
      adminService.updateTournament(id, updatedTournament);
    } catch (Exception e) {
      // return error page
      System.out.println(e.getMessage());
      // return "error";
    }

    // Redirect back to the tournament list after updating
    return "redirect:/admin/admin-tournaments";
  }

  @DeleteMapping("/tournaments/{id}")
  public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
    try {
      adminService.deleteTournament(id);
    } catch (Exception e) {
      // error handling
      System.out.println(e.getMessage());
      // return "error";
    }

    return ResponseEntity.noContent().build(); // Respond with 204 No Content
  }

  private List<List<Player>> playerGroups = new ArrayList<>();

  @GetMapping("/startTournament/{id}")
  public String startTournament(@PathVariable Long id, Model model)
      throws JsonProcessingException {

    // Use RestTemplate to call the API and fetch the tournament details
    try {
      Map<String, Object> tournamentData = adminService.getTournamentById(id);
    } catch (Exception e){
      System.out.println(e.getMessage());
    }

    // Retrieve player list from the tournament
    int round = (int) tournamentData.get("round");

    // Get player list
    List<Player> players = adminService.getPlayerList(tournamentData);

    // Prepare the payload for the matchmaking API
    Map<String, Object> payload = new HashMap<>();
    payload.put("tournamentId", id);
    payload.put("players", players);

    List<List<Map<String, Object>>> rawPlayerGroups = new ArrayList<>();
    try {
      // Get the response body and cast it as a List<List<Map<String, Object>>>
      rawPlayerGroups = adminService.getFirstRoundGroup(payload);
    } catch (JsonProcessingException e) {
      // Handle JSON processing exception
      e.printStackTrace();
      return "error";
    }

    // Now iterate through the rawPlayerGroups as expected
    playerGroups = new ArrayList<>();
    List<List<User>> userGroups = new ArrayList<>();
    for (List<Map<String, Object>> groupMap : rawPlayerGroups) { // Treat groupMap as List<Map<String, Object>>
      List<Player> group = new ArrayList<>();
      List<User> userSubGroup = new ArrayList<>();
      for (Map<String, Object> playerMap : groupMap) { // Iterate over the list directly
        Long playerId = ((Number) playerMap.get("id")).longValue();
        int rank = (int) playerMap.get("rank");
        Player player = new Player(playerId, rank);
        group.add(player);

        User user = userService.findById(player.getId());
        userSubGroup.add(user);
      }
      playerGroups.add(group);
      userGroups.add(userSubGroup);
    }

    // Add player groups and tournament details to the model
    model.addAttribute("round", round);
    model.addAttribute("tournament", tournamentData);
    model.addAttribute("userGroups", userGroups);

    return "admin/startTournament"; // Return the view

  }

  @PostMapping("/startTournament/{id}")
  public String nextRound(@PathVariable Long id,
      Model model) {

    String apiUrl = "http://localhost:8080/tournaments/" + id;
    Map<String, Object> tournamentData = restTemplate.getForObject(apiUrl, Map.class);
    int round = (int) tournamentData.get("round");

    // Increment the round for the next round
    round++;

    // Construct the updated tournament data
    Map<String, Object> updatedTournament = new HashMap<>();

    updatedTournament.put("id", id);
    updatedTournament.put("name", tournamentData.get("name"));
    updatedTournament.put("date", tournamentData.get("date"));
    updatedTournament.put("rankRange", tournamentData.get("rankRange"));
    updatedTournament.put("status", tournamentData.get("status"));
    updatedTournament.put("region", tournamentData.get("region"));
    updatedTournament.put("playerList", tournamentData.get("playerList"));
    updatedTournament.put("round", round); // Incremented round

    if (round >= 4) {
      updatedTournament.put("id", id);
      updatedTournament.put("name", tournamentData.get("name"));
      updatedTournament.put("date", tournamentData.get("date"));
      updatedTournament.put("rankRange", tournamentData.get("rankRange"));
      updatedTournament.put("status", "Closed");
      updatedTournament.put("region", tournamentData.get("region"));
      updatedTournament.put("playerList", tournamentData.get("playerList"));
      updatedTournament.put("round", round); // Incremented round
      String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
      restTemplate.put(tournamentApiUrl, updatedTournament);
      return "redirect:/admin";
    }

    // Send a PUT request to update the tournament
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.put(tournamentApiUrl, updatedTournament);

    // Prepare the payload for the matchmaking API
    Map<String, Object> payload = new HashMap<>();
    payload.put("tournamentId", id);
    payload.put("playerGroups", playerGroups);
    payload.put("round", round);

    // API URL to send the player list to matchmaking API for the next round
    String matchmakingApiUrl = "http://localhost:8080/matchmaking/next-round";

    try {
      // Create the ObjectMapper for JSON serialization
      ObjectMapper objectMapper = new ObjectMapper();

      // Serialize the payload into a JSON string
      String jsonPayload = objectMapper.writeValueAsString(payload);

      // Set the headers with Content-Type as application/json
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // Create the HttpEntity containing the headers and the JSON payload
      HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

      // Send the POST request to the matchmaking API and get the response
      ResponseEntity<List> response = restTemplate.exchange(
          matchmakingApiUrl,
          HttpMethod.POST,
          entity,
          List.class);

      // Get the response body and cast it as a List<List<Map<String, Object>>>
      List<List<Map<String, Object>>> rawPlayerGroups = (List<List<Map<String, Object>>>) response.getBody();

      // Process the rawPlayerGroups into List<List<Player>> as in the GET method
      playerGroups = new ArrayList<>();
      List<List<User>> userGroups = new ArrayList<>();

      for (List<Map<String, Object>> groupMap : rawPlayerGroups) {
        List<Player> group = new ArrayList<>();
        List<User> userSubGroup = new ArrayList<>();
        for (Map<String, Object> playerMap : groupMap) {
          Long playerId = ((Number) playerMap.get("id")).longValue();
          int rank = (int) playerMap.get("rank");
          Player player = new Player(playerId, rank);

          // update user rank
          User user = userService.findById(player.getId());
          // doesnt work need try
          // .orElseThrow(() -> new RuntimeException("User not found: " +
          // player.getId()));
          user.setRank(player.getRank());
          userService.save(user);
          userSubGroup.add(user);

          group.add(player);

        }
        userGroups.add(userSubGroup);
        playerGroups.add(group);
      }

      // Add round, tournament data, and player groups to the model
      model.addAttribute("round", round);
      model.addAttribute("tournament", tournamentData);
      model.addAttribute("userGroups", userGroups);

      return "admin/startTournament"; // Return the updated view

    } catch (JsonProcessingException e) {
      // Handle JSON processing exception
      e.printStackTrace();
      return "/error";
    }
  }

}
