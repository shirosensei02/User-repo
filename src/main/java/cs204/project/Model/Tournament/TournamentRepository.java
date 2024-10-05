package cs204.project.model.tournament;
import cs204.project.model.player.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.dao.EmptyResultDataAccessException;

public interface TournamentRepository extends JpaRepository<Tournament, Long>{

    Long addPlayer(Player player, Tournament tournament);
    int update(Tournament tournament);
    @Query("SELECT * FROM tournament t inner join player p on t.region = p.region where p.id = ?1", nativeQuery = true)

    int deleteById(Long id);
    
    @Query("SELECT COUNT(t) FROM Tournament t WHERE t.region = :region")
    long countByRegion(@Param("region") String region);
}
