package cs204.project.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.dao.DataAccessException;

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
    String tournamentApiUrl = "http://localhost:8080/tournaments";
    // String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments";

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);
    return tournaments;
  }

  public Map<String, Object> getTournamentById(Long id) throws HttpClientErrorException {
    // String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments/" + id;
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
    Map<String, Object> tournament = restTemplate.getForObject(tournamentApiUrl, Map.class);
    return tournament;
  }

  public void addTournament(Map<String, Object> tournamentData) throws HttpClientErrorException, DataAccessException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(tournamentData, headers);
    // String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments/";
    String tournamentApiUrl = "http://localhost:8080/tournaments";
    restTemplate.exchange(tournamentApiUrl, HttpMethod.POST, request, String.class);
  }

  public void updateTournament(Long id, Map<String, Object> updatedTournament) throws HttpClientErrorException {
    // String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments/" + id;
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
    restTemplate.put(tournamentApiUrl, updatedTournament);
  }

  public void deleteTournament(Long id) throws HttpClientErrorException {
    // String tournamentApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/tournaments/" + id;
    String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
    restTemplate.delete(tournamentApiUrl);
  }

  public List<Player> getPlayerList(Map<String, Object> tournamentData) {
    List<?> playerListRaw = (List<?>) tournamentData.get("playerList");
    List<Long> playerList = new ArrayList<>();

    for (Object playerIdObj : playerListRaw) {
      if (playerIdObj instanceof Integer) {
        playerList.add(((Integer) playerIdObj).longValue());
      } else if (playerIdObj instanceof Long) {
        playerList.add((Long) playerIdObj);
      } else {
        System.out.println("Unexpected player ID type: " + playerIdObj.getClass().getName());
      }
    }

    List<Player> players = new ArrayList<>();
    for (Long playerId : playerList) {
      Player playerData = new Player(playerId, userService.findById(playerId).getRank());
      players.add(playerData);
    }

    return players;
  }

  public List<List<Map<String, Object>>> getFirstRoundGroup(Map<String, Object> payload)
      throws JsonProcessingException, HttpClientErrorException {
    String matchmakingApiUrl = "http://localhost:8080/matchmaking/first-round";
    // String matchmakingApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/matchmaking/first-round";
    List<List<Map<String, Object>>> rawPlayerGroups = getRawPlayerGroups(matchmakingApiUrl, payload);
    return rawPlayerGroups;
  }

  public List<List<Map<String, Object>>> getNextRoundGroup(Map<String, Object> payload)
      throws JsonProcessingException, HttpClientErrorException {
    String matchmakingApiUrl = "http://localhost:8080/matchmaking/next-round";
    // String matchmakingApiUrl = "https://tournament-matchmaking-api-gateway.azuremicroservices.io/matchmaking/next-round";
    List<List<Map<String, Object>>> rawPlayerGroups = getRawPlayerGroups(matchmakingApiUrl, payload);
    return rawPlayerGroups;
  }

  public List<List<Map<String, Object>>> getRawPlayerGroups(String matchmakingApiUrl, Map<String, Object> payload)
      throws JsonProcessingException, HttpClientErrorException{
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonPayload = objectMapper.writeValueAsString(payload);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
    ResponseEntity<List> response = restTemplate.exchange(matchmakingApiUrl, HttpMethod.POST, entity, List.class);
    List<List<Map<String, Object>>> rawPlayerGroups = (List<List<Map<String, Object>>>) response.getBody();
    return rawPlayerGroups;
  }
}