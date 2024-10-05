package cs204.project.model.tournament;
import cs204.project.model.player.Player;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournaments;

    public TournamentService(TournamentRepository tournaments){
      this.tournaments = tournaments;
    }

    public List<Tournament> getTournamentList() {
      return tournaments.findAll();
    }

    
    public Tournament addTournament(Tournament tournament) {
      // tournament.setId(tournaments.save(tournament));

      return tournaments.save(tournament);
    }

    
    public int deleteTournament(Long id) {
      return tournaments.deleteById(id);
    }

    
    public Tournament getTournament(Long id) {
      Optional<Tournament> t = tournaments.findById(id);

      if (t.isPresent()){
        return t.get();
      }
      return null;
    }

    
    public Tournament updateTournament(Long id, Tournament tournament) {
      // TODO Auto-generated method stub
      
      return null;
    }

    
    public Long joinTournament(Player player, Tournament tournament) {
      return tournaments.addPlayer(player, tournament);
    }
    
}
