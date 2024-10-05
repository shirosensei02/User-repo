package cs204.project.Controller;

import cs204.project.Model.Tournament.Tournament;
import cs204.project.Model.Tournament.TournamentService;
import cs204.project.Model.Player.*;
// import cs204.project.Exception.TournamentIsFullException;
// import cs204.project.Exception.TournamentNotFoundException;
import cs204.project.Exception.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    // private TournamentService tournamentService;
    private PlayerService playerService;

    public PlayerController(PlayerService playerService){
      this.playerService = playerService;
    }

    /**
     * List all tournaments in system
     * @return list of all tournaments
     */
    @GetMapping("/players")
    public List<Player> getPlayers(){
      return playerService.getPlayerList();
    }

    /**
     * Search tournament with given id
     * if not found, throw TournamentNotFoundException
     * @param id
     * @return book with given id
     */
    @GetMapping("players/{id}")
    public Player getPlayer(@PathVariable Long id){
      return playerService.getPlayer(id);
    }

        /**
     * Add a new tournament with POST request to "/tournaments"
     * Note the use of @RequestBody
     * @param tournament
     * @return list of all tournament
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/players")
    public Player addPlayer(@RequestBody Player player){
        System.out.println(player);
        return playerService.addPlayer(player);
    }

    /**
     * If there is no tournament with the given "id", throw a TournamentNotFoundException
     * @param id
     * @param newTournamentInfo
     * @return the updated, or newly added tournament
     */
    @PutMapping("/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player newPlayerInfo){
        Player player = playerService.updatePlayer(id, newPlayerInfo);
        if(player == null) throw new PlayerNotFoundException(id);
        
        return player;
    }

    /**
     * Remove a tournament with the DELETE request to "/tournament/{id}"
     * If there is no tournament with the given "id", throw a TournamentNotFoundException
     * 
     * @param id
     */
    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable long id) {
      if (playerService.deletePlayer(id) == 0) throw new PlayerNotFoundException(id);
    }

    
}
