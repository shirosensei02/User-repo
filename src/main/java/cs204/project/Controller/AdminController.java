package cs204.project.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cs204.project.Entity.User;
import cs204.project.Service.AdminService;
import cs204.project.Service.UserDetailService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/admin")
public class AdminController {

  @Autowired
  private UserDetailService userService;

  @Autowired
  private AdminService adminService;

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

  @GetMapping("/tournament")
  public String showAddTournamentPage(Model model) {
    return "admin/addTournament";
  }

  @PostMapping("/tournament")
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

    // Removed try-catch block to allow exceptions to propagate
    adminService.addTournament(tournamentData);

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

  @GetMapping("/tournament/{id}")
  public String getUpdateTournament(@PathVariable("id") Long id, Model model) {
    Map<String, Object> tournament = adminService.getTournamentById(id);
    // Pass the tournament data to the Thymeleaf view
    model.addAttribute("tournament", tournament);
    return "admin/updateTournament"; // Returns the update form page
  }

  // Handle the form submission for updating the tournament
  @PostMapping("/tournament/{id}")
  public String updateTournament(
      @PathVariable Long id,
      @RequestParam String name,
      @RequestParam String datetime,
      @RequestParam int minRank,
      @RequestParam int maxRank,
      @RequestParam(defaultValue = "Open") String status,
      @RequestParam String region) {

    // Construct the updated tournament data
    Map<String, Object> updatedTournament = createUpdatedTournament(id, name, datetime, minRank, maxRank, status,
        region);

    adminService.updateTournament(id, updatedTournament);

    // Redirect back to the tournament list after updating
    return "redirect:/admin/admin-tournaments";
  }

  @DeleteMapping("/tournaments/{id}")
  public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
    // Removed try-catch block to allow exceptions to propagate
    adminService.deleteTournament(id);
    return ResponseEntity.noContent().build(); // Respond with 204 No Content
  }

  private List<List<Player>> playerGroups = new ArrayList<>();

  @GetMapping("/tournament-start/{id}")
  public String startTournament(@PathVariable Long id, Model model) throws JsonProcessingException{
    Map<String, Object> tournamentData = adminService.getTournamentById(id);

    // Retrieve player list from the tournament
    int round = (int) tournamentData.get("round");

    // Get player list
    List<Player> players = adminService.getPlayerList(tournamentData);

    // Prepare the payload for the matchmaking API
    Map<String, Object> payload = new HashMap<>();
    payload.put("tournamentId", id);
    payload.put("players", players);

    List<List<Map<String, Object>>> rawPlayerGroups = adminService.getFirstRoundGroup(payload);
    List<List<User>> userGroups = getUserGroups(rawPlayerGroups);

    // Add player groups and tournament details to the model
    model.addAttribute("round", round);
    model.addAttribute("tournament", tournamentData);
    model.addAttribute("userGroups", userGroups);

    return "admin/startTournament"; // Return the view
  }

  @PostMapping("/tournament-start/{id}")
  public String nextRound(@PathVariable Long id, @RequestParam("groupRanks") String groupRanksJson, Model model) throws JsonProcessingException{
    // Initialize ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();

    // Parse the JSON string into a list of lists of maps
    playerGroups = objectMapper.readValue(groupRanksJson, new TypeReference<>() {
    });

    // Get Tournament Details
    Map<String, Object> tournamentData = adminService.getTournamentById(id);

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
      adminService.updateTournament(id, updatedTournament);
      return "redirect:/admin";
    }

    // Send a PUT request to update the tournament
    adminService.updateTournament(id, updatedTournament);

    // Prepare the payload for the matchmaking API
    Map<String, Object> payload = new HashMap<>();
    payload.put("tournamentId", id);
    payload.put("playerGroups", playerGroups);
    payload.put("round", round);

    List<List<Map<String, Object>>> rawPlayerGroups = adminService.getNextRoundGroup(payload);
    List<List<User>> userGroups = getUserGroups(rawPlayerGroups);

    // Add round, tournament data, and player groups to the model
    model.addAttribute("round", round);
    model.addAttribute("tournament", tournamentData);
    model.addAttribute("userGroups", userGroups);

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