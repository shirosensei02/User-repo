package cs204.project.Model.Player;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

public interface PlayerService {

    UserDetails loadUserByUsername(String username);
    
    Player getPlayer(Long id);
    List<Player> getPlayerList();

    Integer getPlayerRank(Long id, String region);
    
    

    // return newly added Player
    Player addPlayer(Player player);

    /**  return updated tournament
    @param id
    @param tournament
    @return
    */ 
    Player updatePlayer(Long id, Player player);

    /**
     * return status of delete
     * 1 if remove
     * 0 if does not exist
     * @param id
     * @return
     */
    int deletePlayer(Long id);


} 
