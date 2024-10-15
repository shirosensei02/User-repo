package cs204.project.Service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import cs204.project.Entity.User;
import cs204.project.Controller.Player;

@Service
public class AdminService {

    @Autowired
    private UserDetailService userService;

    private RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> getTournaments() {
        String tournamentApiUrl = "http://localhost:8080/tournaments";
        List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);
        tournaments.sort(Comparator.comparing(t -> (String) t.get("date")));
        return tournaments;
    }

    public void addTournament(Map<String, Object> tournamentData) {
        String tournamentApiUrl = "http://localhost:8080/tournaments";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(tournamentData, headers);
        restTemplate.exchange(tournamentApiUrl, HttpMethod.POST, request, String.class);
    }

    public Map<String, Object> getTournamentById(Long id) {
        String apiUrl = "http://localhost:8080/tournaments/" + id;
        return restTemplate.getForObject(apiUrl, Map.class);
    }

    public void updateTournament(Long id, Map<String, Object> updatedTournament) {
        String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
        restTemplate.put(tournamentApiUrl, updatedTournament);
    }

    public void deleteTournament(Long id) {
        String tournamentApiUrl = "http://localhost:8080/tournaments/" + id;
        restTemplate.delete(tournamentApiUrl);
    }

    public List<List<Player>> startTournament(Long id, List<Long> playerList, int round) throws Exception {
        List<Player> players = new ArrayList<>();
        for (Long playerId : playerList) {
            Player playerData = new Player(playerId, userService.findById(playerId).getRank());
            players.add(playerData);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("tournamentId", id);
        payload.put("players", players);

        String matchmakingApiUrl = "http://localhost:8080/matchmaking/first-round";
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(payload);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        ResponseEntity<List> response = restTemplate.exchange(matchmakingApiUrl, HttpMethod.POST, entity, List.class);
        List<List<Map<String, Object>>> rawPlayerGroups = (List<List<Map<String, Object>>>) response.getBody();

        List<List<Player>> playerGroups = new ArrayList<>();
        for (List<Map<String, Object>> groupMap : rawPlayerGroups) {
            List<Player> group = new ArrayList<>();
            for (Map<String, Object> playerMap : groupMap) {
                Long playerId = ((Number) playerMap.get("id")).longValue();
                int rank = (int) playerMap.get("rank");
                Player player = new Player(playerId, rank);
                group.add(player);
            }
            playerGroups.add(group);
        }
        return playerGroups;
    }

    public List<List<Player>> nextRound(Long id, int currentRound) throws Exception {
        // Get current tournament data
        Map<String, Object> tournamentData = getTournamentById(id);
        List<Long> playerList = (List<Long>) tournamentData.get("playerList");

        // Recalibrate player rankings based on previous round results (this should be
        // your logic)
        List<Player> recalibratedPlayers = new ArrayList<>();
        for (Long playerId : playerList) {
            Player player = new Player(playerId, userService.findById(playerId).getRank());
            // Perform recalibration logic here based on previous round
            recalibratePlayerRank(player); // hypothetical function
            recalibratedPlayers.add(player);
        }

        // Create the next round's matches by splitting recalibrated players into groups
        Map<String, Object> payload = new HashMap<>();
        payload.put("tournamentId", id);
        payload.put("players", recalibratedPlayers);

        String matchmakingApiUrl = "http://localhost:8080/matchmaking/next-round";
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(payload);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        ResponseEntity<List> response = restTemplate.exchange(matchmakingApiUrl, HttpMethod.POST, entity, List.class);
        List<List<Map<String, Object>>> rawPlayerGroups = (List<List<Map<String, Object>>>) response.getBody();

        List<List<Player>> nextRoundPlayerGroups = new ArrayList<>();
        for (List<Map<String, Object>> groupMap : rawPlayerGroups) {
            List<Player> group = new ArrayList<>();
            for (Map<String, Object> playerMap : groupMap) {
                Long playerId = ((Number) playerMap.get("id")).longValue();
                int rank = (int) playerMap.get("rank");
                Player player = new Player(playerId, rank);
                group.add(player);
            }
            nextRoundPlayerGroups.add(group);
        }

        return nextRoundPlayerGroups;
    }

    private void recalibratePlayerRank(Player player) {
        // Example recalibration logic (this should be your actual implementation)
        int newRank = player.getRank() + 10; // Just a simple example; your logic will vary
        player.setRank(newRank);
    }

}
