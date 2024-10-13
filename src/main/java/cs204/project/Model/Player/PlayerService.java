package cs204.project.model.player;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

public interface PlayerService {
    public Player createPlayer(Player player);
    public Player getPlayer(Long id);
    public List<Player> getPlayerList();
    public Player updatePlayer(Long id, Player player);
    public void deletePlayer(Long id);

    public void registerUser(Player newPlayer);
    UserDetails loadUserByUsername(String username);
} 
