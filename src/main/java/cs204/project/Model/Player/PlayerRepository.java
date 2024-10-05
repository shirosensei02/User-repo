package cs204.project.Model.Player;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cs204.project.Model.Tournament.Tournament;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.EmptyResultDataAccessException;

@Repository
public interface PlayerRepository /*extends JpaRepository<Player, Long> */{

    int deleteById(Long id);
    List<Player> findAll();
    Optional<Player> findById(Long id);
    Optional<Integer> findPlayerRank(Long id, String region);
    Optional<Player> findByUsername(String username);
    Long save(Player player);
    int update(Player player);

}
