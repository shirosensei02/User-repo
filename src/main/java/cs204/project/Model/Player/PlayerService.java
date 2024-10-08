package cs204.project.Model.Player;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

public interface PlayerService {

    UserDetails loadUserByUsername(String username);
    
    Player getPlayer(Long id);
    List<Player> getPlayerList();
    // Integer getPlayerRank(Long id, String region);
    Player addPlayer(Player player);
    Player updatePlayer(Long id, Player player);
    int deletePlayer(Long id);


} 
