package cs204.project.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("")
    @ResponseBody
    public String getDashboard(){
        return "This is admin dashboard";
    }
    
    @GetMapping("/admin-tournaments")
    public String getTournaments(){
        return "admin-tournaments";
    }
    
    @GetMapping("/addTournament")
    public String showAddTournamentPage(Model model) {
        return "addTournament";
    }

    // @PostMapping("/addTournament")
    // public String postMethodName(
    //     @RequestParam String name,
    //     @RequestParam String datetime,
    //     @RequestParam String rankRange,
    //     @RequestParam(defaultValue = "Active") String status,
    //     @RequestParam String region) {

    //     //TODO: process POST request

    //     // create new tournament object
    //     // Tournament tournament = new Tournament();
    //     // tournament.setName(name);
    //     // tournament.setDatetime(datetime);
    //     // tournament.setRankRange(rankRange);
    //     // tournament.setStatus(status);
    //     // tournament.setRegion(region);

    //     // save it to database
    //     // tournamentService.saveTournament(tournament);

    //     return "redirect:/admin/admin-tournaments";
    // }
    
}

