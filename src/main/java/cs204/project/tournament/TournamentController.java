// package cs204.project.tournament;

// import java.util.List;

// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// public class TournamentController {
//     private TournamentService tournamentService;

//     public TournamentController(TournamentService tournamentService){
//       this.tournamentService = tournamentService;
//     }

//     /**
//      * List all tournaments in system
//      * @return list of all tournaments
//      */
//     @GetMapping("/tournaments")
//     public List<Tournament> getTournaments(){
//       return tournamentService.getTournamentList();
//     }

//     /**
//      * Search tournament with given id
//      * if not found, throw TournamentNotFoundException
//      * @param id
//      * @return book with given id
//      */
//     @GetMapping("tournaments/{id}")
//     public Tournament getTournament(@PathVariable Long id){
//       return null;
//     }

//         /**
//      * Add a new book with POST request to "/tournaments"
//      * Note the use of @RequestBody
//      * @param tournament
//      * @return list of all tournament
//      */
//     @ResponseStatus(HttpStatus.CREATED)
//     @PostMapping("/tournaments")
//     public Tournament addTournament(@RequestBody Tournament tournament){
//         return tournamentService.addTournament(tournament);
//     }

//     /**
//      * If there is no tournament with the given "id", throw a TournamentNotFoundException
//      * @param id
//      * @param newTournamentInfo
//      * @return the updated, or newly added tournament
//      */
//     @PutMapping("/tournaments/{id}")
//     public Tournament updateTournament(@PathVariable Long id, @RequestBody Tournament newTournamentInfo){
//         Tournament tournament = tournamentService.updateTournament(id, newTournamentInfo);
//         if(tournament == null) throw new TournamentNotFoundException(id);
        
//         return tournament;
//     }

//     /**
//      * Remove a tournament with the DELETE request to "/tournament/{id}"
//      * If there is no tournament with the given "id", throw a TournamentNotFoundException
//      * 
//      * @param id
//      */
//     @DeleteMapping("/tournaments/{id}")
//     public void deleteTournament(@PathVariable Long id) {
//       if (tournamentService.deleteTournament(id) == 0)
//         throw new TournamentNotFoundException(id);
//     }
// }
