package cs204.project.model.player;

import java.util.Optional;
import java.util.List;

// import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
// import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;




// import cs204.project.model.player.Player.userRole;
// import lombok.AllArgsConstructor;

@Service
// @AllArgsConstructor
public class PlayerServiceImpl implements PlayerService, UserDetailsService {

    private final PlayerRepository players;
    private final BCryptPasswordEncoder passwordEncoder;

    // @Autowired
    public PlayerServiceImpl (PlayerRepository players, BCryptPasswordEncoder passwordEncoder) {
    this.players = players;
    this.passwordEncoder = passwordEncoder;
    }

    
    @Override
    public Player createPlayer(Player player) {
        // return players.save(player);
        return players.findById(player.getId()).isPresent() ? null : players.save(player);
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
    public Player updatePlayer(Long id, Player player) {
        Optional<Player> p = players.findById(id);
        return p.isPresent() ? players.save(player) : null;
    }

    @Override
    public void deletePlayer(Long id) {
        players.deleteById(id);
    }

    @Override
    public void registerUser(Player newPlayer) {
        if (newPlayer == null) {
            throw new RuntimeException("Invalid user name or password");
        }
        newPlayer.setPassword(passwordEncoder.encode(newPlayer.getPassword()));
        // newPlayer.setRole(userRole.USER);
        newPlayer.setRole("USER");
        // newPlayer.setRank(0); // DONT DELETE
        players.save(newPlayer);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player user = players.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // return User.builder()
        // .username(user.getUsername())
        // .password(user.getPassword())
        // .roles("USER") // Pass just "USER", not "ROLE_USER"
        // .build();
        return new PlayerCustom(user);
    }

    // @Override
    // public Integer getPlayerRank(Long id, String region) {
    // Optional<Integer> rank = players.findPlayerRank(id, region);
    // return rank.isPresent() ? rank.get() : null;
    // }

}