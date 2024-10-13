package cs204.project.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import cs204.project.model.player.PlayerService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PlayerService players;

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("")
    public String getDashboard(){
        return "admin/home";
    }

    @GetMapping("/tournaments")
    public String getTournaments(Model model) {
    // RestTemplate restTemplate = new RestTemplate();

    // URL of the tournament service through gateway
    String tournamentApiUrl = "http://localhost:8080/tournaments";

    // Fetch tournaments as a list of maps (JSON objects)
    List<Map<String, Object>> tournaments = restTemplate.getForObject(tournamentApiUrl, List.class);
    tournaments.sort(Comparator.comparing(t -> (String) t.get("date"))); // Adjust according to your date format

    // Pass the fetched tournaments to the Thymeleaf view
    model.addAttribute("adminTournaments", tournaments);

    return "admin/tournaments"; // This returns the tournaments.html Thymeleaf view
  }
}
