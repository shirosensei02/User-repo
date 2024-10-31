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
    tournaments.sort(Comparator.comparing(t -> (String) t.get("date")));

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
    Map<String, Object> updatedTournament = createUpdatedTournament(id, name, datetime, minRank, maxRank, status,
        region);

    try {
      adminService.updateTournament(id, updatedTournament);
    } catch (Exception e) {
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
      System.out.println(e.getMessage());
      // return "error";
    }

    return ResponseEntity.noContent().build(); // Respond with 204 No Content
  }

  @GetMapping("/startTournament/{id}")
  public String startTournament(@PathVariable Long id, Model model) {

    Map<String, Object> tournamentData = new HashMap<>();
    try {
      tournamentData = adminService.getTournamentById(id);
    } catch (Exception e) {
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
      rawPlayerGroups = adminService.getFirstRoundGroup(payload);
    } catch (Exception e) {
      e.printStackTrace();
      // return "error";
    }

    List<List<User>> userGroups = getUserGroups(rawPlayerGroups);

    // Add player groups and tournament details to the model
    model.addAttribute("round", round);
    model.addAttribute("tournament", tournamentData);
    model.addAttribute("userGroups", userGroups);

    return "admin/startTournament"; // Return the view

  }

  @PostMapping("/startTournament/{id}")
  public String nextRound(@PathVariable Long id,
      @RequestParam Map<String, String> placements,
      Model model) {

    Map<String, Object> tournamentData = new HashMap<>();
    try {
      tournamentData = adminService.getTournamentById(id);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    // Increment the round for the next round
    int round = (int) tournamentData.get("round");
    round++;

    // Construct the updated tournament data
    Map<String, Object> updatedTournament = createUpdatedTournament(id,
        (String) tournamentData.get("name"),
        (String) tournamentData.get("date"), 0, 0,
        (String) tournamentData.get("status"),
        (String) tournamentData.get("region"));
    updatedTournament.put("rankRange", tournamentData.get("rankRange"));
    updatedTournament.put("playerList", tournamentData.get("playerList"));
    updatedTournament.put("round", round); // Incremented round

    if (round >= 4) {
      updatedTournament.put("status", "Closed");
      try {
        adminService.updateTournament(id, updatedTournament);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        // return "error";
      }
      return "redirect:/admin";
    }

    // Create a list to hold all players and their placements
    List<Map<String, Object>> allPlayers = new ArrayList<>();

    // Populate all players with their placements
    for (Map.Entry<String, String> entry : placements.entrySet()) {
        String playerId = entry.getKey().split("_")[1]; // Extract the player ID
        String placement = entry.getValue(); // Get the selected placement
        System.out.println("This is the placement");

        // Create a new map for the player's information
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("id", playerId);
        playerData.put("rank", userService.findById(playerId).getRank());
        playerData.put("placement", Integer.parseInt(placement)); // Store as integer for sorting
        allPlayers.add(playerData);
    }

    // Sort all players based on their placements
    allPlayers.sort(Comparator.comparingInt(p -> (Integer) p.get("placement")));

    // Create a map to hold players by their placements
    Map<Integer, List<Map<String, Object>>> placementMap = new HashMap<>();
    for (Map<String, Object> player : allPlayers) {
        int placement = (Integer) player.get("placement");
        placementMap.putIfAbsent(placement, new ArrayList<>());
        placementMap.get(placement).add(player);
    }

    // Create groups
    List<List<Integer>> groups = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
        groups.add(new ArrayList<>());
    }

    // Distribute players into groups
    boolean done = false;
    while (!done) {
        done = true;
        for (int placement = 1; placement <= 8; placement++) {
            if (placementMap.containsKey(placement) && !placementMap.get(placement).isEmpty()) {
                for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
                    if (groups.get(groupIndex).size() < 4) { // Up to 4 distinct placements in each group
                        // Add the player to the group
                        groups.get(groupIndex).add(placement);
                        // Remove the player from the placementMap
                        placementMap.get(placement).remove(0); // Remove one player from that placement
                        done = false; // Continue looping until all placements are distributed
                        break; // Break to avoid adding multiple players from the same placement to one group
                    }
                }
            }
        }
    }

    // Send a PUT request to update the tournament
    try {
      adminService.updateTournament(id, updatedTournament);

      // Prepare the payload for the matchmaking API
      Map<String, Object> payload = new HashMap<>();
      payload.put("tournamentId", id);
      payload.put("playerGroups", groupedPlayers);
      payload.put("round", round);

      List<List<Map<String, Object>>> rawPlayerGroups = adminService.getNextRoundGroup(payload);
      
      List<List<User>> userGroups = getUserGroups(rawPlayerGroups);
      
      // Add round, tournament data, and player groups to the model
      model.addAttribute("round", round);
      model.addAttribute("tournament", tournamentData);
      model.addAttribute("userGroups", userGroups);
      
    } catch (Exception e) {
      System.out.println(e.getMessage());
      // return "error"
    }
    return "admin/startTournament"; // Return the updated view

  }

  public Map<String, Object> createUpdatedTournament(Long id, String name, String datetime, int minRank, int maxRank,
      String status, String region) {
    Map<String, Object> updatedTournament = new HashMap<>();
    updatedTournament.put("id", id);
    updatedTournament.put("name", name);
    updatedTournament.put("date", datetime);
    updatedTournament.put("rankRange", new int[] { minRank, maxRank });
    updatedTournament.put("status", status);
    updatedTournament.put("region", region);

    return updatedTournament;
  }

  public List<List<User>> getUserGroups(List<List<Map<String, Object>>> rawPlayerGroups) {
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

    return userGroups;
  }
}
