package cs204.project.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cs204.project.Entity.User;
import cs204.project.Service.UserDetailService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserDetailService userService;

    @GetMapping("")
    public String getDashboard(){
        return "admin/admin-dashboard";
    }
    
    @GetMapping("/admin-tournaments")
    public String getTournaments(){
        return "admin/admin-tournaments";
    }
    
    @GetMapping("/addTournament")
    public String showAddTournamentPage(Model model) {
        return "admin/addTournament";
    }
    
    @PostMapping("/addTournament")
    public String postMethodName(
        @RequestParam String name,
        @RequestParam String datetime,
        @RequestParam String rankRange,
        @RequestParam(defaultValue = "Active") String status,
        @RequestParam String region) {

        //TODO: process POST request

        // create new tournament object
        // Tournament tournament = new Tournament();
        // tournament.setName(name);
        // tournament.setDatetime(datetime);
        // tournament.setRankRange(rankRange);
        // tournament.setStatus(status);
        // tournament.setRegion(region);

        // save it to database
        // tournamentService.saveTournament(tournament);

        return "redirect:/admin/admin-tournaments";
    }

    @GetMapping("/user-management")
    public String getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size,
        Model model) {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userService.findAllUsers(pageable);
            
            model.addAttribute("users", userPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", userPage.getTotalPages());
            
            return "admin/user-management";
    }
    
}

