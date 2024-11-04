package cs204.project.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

import cs204.project.Controller.Player;
import cs204.project.Service.UserDetailService;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AdminService {

  private RestTemplate restTemplate = new RestTemplate();

  @Autowired
  private UserDetailService userService;

  public List<Map<String, Object>> getAllTournaments() {
    // URL of the tournament service through gateway
    String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments";

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);

    return tournaments;
  }

  public Map<String, Object> getTournamentById(Long id) throws Exception {

    // Fetch the tournament data by ID
    String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments/" + id;
    try {
      Map<String, Object> tournament = restTemplate.getForObject(tournamentApiUrl, Map.class);
      return tournament;
    } catch (Exception e) {
      throw e;
    }

  }

  public void addTournament(Map<String, Object> tournamentData) throws Exception {
    // Create headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // Create a request entity (tournament data with headers)
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(tournamentData, headers);

    // URL of the Tournament API
    String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments";

    // Send POST request to the Tournament API
    try {
      restTemplate.exchange(tournamentApiUrl, HttpMethod.POST, request, String.class);
    } catch (Exception e) {
      throw e;
    }
  }

  public void updateTournament(Long id, Map<String, Object> updatedTournament) throws Exception {
    try {
      // Send a PUT request to update the tournament
      String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments/" + id;
      restTemplate.put(tournamentApiUrl, updatedTournament);
    } catch (Exception e) {
      throw e;
    }
  }

  public void deleteTournament(Long id) throws Exception {
    try {
      // URL of the tournament service through gateway
      String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments/" + id;

      // Call the tournament API to delete the tournament
      restTemplate.delete(tournamentApiUrl);
    } catch (Exception e) {
      throw e;
    }
  }

  public List<Player> getPlayerList(Map<String, Object> tournamentData) {
    List<?> playerListRaw = (List<?>) tournamentData.get("playerList");
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

    // Create a list of Player objects
    List<Player> players = new ArrayList<>();
    for (Long playerId : playerList) {
      Player playerData = new Player(playerId, userService.findById(playerId).getRank());
      players.add(playerData);
    }

    return players;
  }

  public List<List<Map<String, Object>>> getFirstRoundGroup(Map<String, Object> payload) throws Exception {
    // API URL to send player list to matchmaking API
    String matchmakingApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/first-round";

    // Call the matchmaking API and get a raw response
    try {
      List<List<Map<String, Object>>> rawPlayerGroups = getRawPlayerGroups(matchmakingApiUrl, payload);
      return rawPlayerGroups;
    } catch (Exception e) {
      throw e;
    }
  }

  public List<List<Map<String, Object>>> getNextRoundGroup(Map<String, Object> payload) throws Exception {
    String matchmakingApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/next-round";

    try {
      List<List<Map<String, Object>>> rawPlayerGroups = getRawPlayerGroups(matchmakingApiUrl, payload);
      return rawPlayerGroups;
    } catch (Exception e) {
      throw e;
    }
  }

  public List<List<Map<String, Object>>> getRawPlayerGroups(String matchmakingApiUrl, Map<String, Object> payload) throws Exception{
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

      List<List<Map<String, Object>>> rawPlayerGroups = (List<List<Map<String, Object>>>) response.getBody();

      return rawPlayerGroups;

    } catch (Exception e) {
      throw e;
    }
  }
}
