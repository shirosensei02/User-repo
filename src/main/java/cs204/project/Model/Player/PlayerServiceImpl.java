package cs204.project.Model.Player;

import java.util.Optional;
import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cs204.project.Model.Tournament.Tournament;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PlayerServiceImpl implements PlayerService, UserDetailsService {

    private final PlayerRepository players;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player user = players.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")  // Pass just "USER", not "ROLE_USER"
                .build();
    }

    @Override
    public Player getPlayer(Long id) {
        Optional<Player> p = players.findById(id);
        return p.isPresent() ? p.get() : null;
    }

    @Override
    public List<Player> getPlayerList() {
        return players.findAll();
    }

    @Override
    public Integer getPlayerRank(Long id, String region) {
        Optional<Integer> rank = players.findPlayerRank(id, region);
        return rank.isPresent() ? rank.get() : null;
    }

    public Player addPlayer(Player player){
        player.setId(players.save(player));
      return player;
    }

    public Player updatePlayer(Long id, Player player) {
        return null;
    }

    public int deletePlayer(Long id) {
        return players.deleteById(id);
    }

}