package cs204.project.controller;

import cs204.project.model.player.*;
// import cs204.project.Exception.TournamentIsFullException;
// import cs204.project.Exception.TournamentNotFoundException;
import cs204.project.exception.*;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
public class PlayerController {
    // private TournamentService tournamentService;
    private PlayerService playerService;

    public PlayerController(PlayerServiceImpl playerService){
      this.playerService = playerService;
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public String getHomePage(){
        return "player/home";
    }

    // @PreAuthorize("hasRole('ADMIN')") //Only admins can see all players
    // @GetMapping("")
    // public List<Player> getPlayers(){
    //   return playerService.getPlayerList();
    // }

    @GetMapping("{id}")
    public Player getPlayer(@PathVariable Long id){
      return playerService.getPlayer(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/players")
    public Player addPlayer(@RequestBody Player player){
        System.out.println(player);
        return playerService.createPlayer(player);
    }

    @PutMapping("/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player newPlayerInfo){
        Player player = playerService.updatePlayer(id, newPlayerInfo);
        if(player == null) throw new PlayerNotFoundException(id);
        
        return player;
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable long id) {
      // if (playerService.deletePlayer(id) == 0) throw new PlayerNotFoundException(id);
      try {
        playerService.deletePlayer(id);
      } catch (EmptyResultDataAccessException e) {
        throw new PlayerNotFoundException(id);
      }
    }

    
}
