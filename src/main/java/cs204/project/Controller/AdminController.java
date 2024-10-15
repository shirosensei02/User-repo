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

  @SuppressWarnings("unchecked")
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

  // private Map<String, Object> tournamentData = null;
  private List<List<Player>> playerGroups = new ArrayList<>();

  @GetMapping("/startTournament/{id}")
  public String startTournament(@PathVariable Long id, Model model)
      throws JsonProcessingException {
    // API URL to get tournament details by ID
    String apiUrl = "http://localhost:8080/tournaments/" + id;

    // Log the API URL
    // System.out.println("Fetching tournament data from: " + apiUrl);

    // Use RestTemplate to call the API and fetch the tournament details
    // RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> tournamentData = restTemplate.getForObject(apiUrl, Map.class);

    // Debugging: Check if the tournament data is retrieved successfully
    // System.out.println("Tournament Data: " + tournamentData);

    // Retrieve player list from the tournament
    int round = (int) tournamentData.get("round");
    List<?> playerListRaw = (List<?>) tournamentData.get("playerList");

    // Debugging: Check the player list retrieved from tournamentData
    // System.out.println("Raw Player List: " + playerListRaw);

    List<Long> playerList = new ArrayList<>();

    // Convert Integer to Long if necessary
    for (Object playerIdObj : playerListRaw) {
      if (playerIdObj instanceof Integer) {
        playerList.add(((Integer) playerIdObj).longValue()); // Convert Integer to Long
      } else if (playerIdObj instanceof Long) {
        playerList.add((Long) playerIdObj); // Cast to Long directly
      } else {
        System.out.println("Unexpected player ID type: " + playerIdObj.getClass().getName());
      }
    }

    // Debugging: Check the final converted player list
    // System.out.println("Converted Player List: " + playerList);

    // Create a list of Player objects
    List<Player> players = new ArrayList<>();
    for (Long playerId : playerList) {
      Player playerData = new Player(playerId, userService.findById(playerId).getRank());
      players.add(playerData);
    }

    // Debugging: Check the list of players created
    // System.out.println("Players List: " + players);

    // Prepare the payload for the matchmaking API
    Map<String, Object> payload = new HashMap<>();
    payload.put("tournamentId", id);
    payload.put("players", players);

    // Debugging: Log the payload before sending it
    // System.out.println("Payload for Matchmaking API: " + payload);

    // API URL to send player list to matchmaking API
    String matchmakingApiUrl = "http://localhost:8080/matchmaking/first-round";
    // System.out.println("Matchmaking API URL: " + matchmakingApiUrl);

    // Call the matchmaking API and get a raw response
    try {
      // Create the ObjectMapper for JSON serialization
      ObjectMapper objectMapper = new ObjectMapper();

      // Serialize the payload into a JSON string
      String jsonPayload = objectMapper.writeValueAsString(payload);
      // System.out.println("Serialized JSON Payload: " + jsonPayload);

      // Set the headers with Content-Type as application/json
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // Create the HttpEntity containing the headers and the JSON payload
      HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

      // Send the POST request with the correct headers
      ResponseEntity<List> response = restTemplate.exchange(
          matchmakingApiUrl,
          HttpMethod.POST,
          entity,
          List.class);

      // Debugging: Log the raw response from the matchmaking API
      // System.out.println("Raw Response from Matchmaking API: " + response);

      // Get the response body and cast it as a List<List<Map<String, Object>>>
      List<List<Map<String, Object>>> rawPlayerGroups = (List<List<Map<String, Object>>>) response.getBody();

      // Now iterate through the rawPlayerGroups as expected
      // List<List<Player>> playerGroups = new ArrayList<>();
      for (List<Map<String, Object>> groupMap : rawPlayerGroups) { // Treat groupMap as List<Map<String, Object>>
        List<Player> group = new ArrayList<>();
        for (Map<String, Object> playerMap : groupMap) { // Iterate over the list directly
          Long playerId = ((Number) playerMap.get("id")).longValue();
          int rank = (int) playerMap.get("rank");
          Player player = new Player(playerId, rank);
          group.add(player);
        }
        playerGroups.add(group);
      }

      // Debugging: Check the final player groups
      // System.out.println("Final Player Groups: " + playerGroups);

      // Add player groups and tournament details to the model
      model.addAttribute("round", round);
      model.addAttribute("tournament", tournamentData);
      model.addAttribute("playerGroups", playerGroups);

      return "admin/startTournament"; // Return the view

    } catch (JsonProcessingException e) {
      // Handle JSON processing exception
      e.printStackTrace();
      return "error"; // You can redirect to an error page if necessary
    }
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
      for (List<Map<String, Object>> groupMap : rawPlayerGroups) {
        List<Player> group = new ArrayList<>();
        for (Map<String, Object> playerMap : groupMap) {
          Long playerId = ((Number) playerMap.get("id")).longValue();
          int rank = (int) playerMap.get("rank");
          Player player = new Player(playerId, rank);
          
          //update user rank
          User user = userService.findById(player.getId());
          // doesnt work need try
              // .orElseThrow(() -> new RuntimeException("User not found: " + player.getId()));
          user.setRank(player.getRank());
          userService.save(user);

          group.add(player);

        }
        playerGroups.add(group);
      }

      // Add round, tournament data, and player groups to the model
      model.addAttribute("round", round);
      model.addAttribute("tournament", tournamentData);
      model.addAttribute("playerGroups", playerGroups);

      return "admin/startTournament"; // Return the updated view

    } catch (JsonProcessingException e) {
      // Handle JSON processing exception
      e.printStackTrace();
      return "error"; // You can redirect to an error page if necessary
    }
  }

}
