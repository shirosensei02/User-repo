package cs204.project.model.tournament;
import cs204.project.model.player.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.dao.EmptyResultDataAccessException;

public interface CustomTournamentRepository {
    Long addPlayer(Player player, Tournament tournament);
    int update(Tournament tournament);
    int deleteById(Long id);
}

