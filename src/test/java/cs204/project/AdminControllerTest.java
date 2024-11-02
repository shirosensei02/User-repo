package cs204.project;

import cs204.project.Controller.AdminController;
import cs204.project.Entity.User;
import cs204.project.Service.AdminService;
import cs204.project.Service.UserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private UserDetailService userService;

    @Mock
    private AdminService adminService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for getDashboard()
    @Test
    public void testGetDashboard() {
        String result = adminController.getDashboard();
        assertEquals("redirect:/admin/admin-tournaments", result);
    }

    // Test for getTournaments()
    @Test
    public void testGetTournaments() {
        List<Map<String, Object>> tournaments = new ArrayList<>();
        Map<String, Object> tournament = new HashMap<>();
        tournament.put("date", "2024-01-01");
        tournaments.add(tournament);

        when(adminService.getAllTournaments()).thenReturn(tournaments);

        String result = adminController.getTournaments(model);

        verify(adminService, times(1)).getAllTournaments();
        verify(model, times(1)).addAttribute("adminTournaments", tournaments);
        assertEquals("admin/admin-tournaments", result);
    }

    // Test for addTournament()
    @Test
    public void testAddTournament() {
        String name = "Test Tournament";
        String datetime = "2024-01-01T10:00:00";
        int minRank = 1;
        int maxRank = 100;
        String status = "Open";
        String region = "US";

        String result = adminController.addTournament(name, datetime, minRank, maxRank, status, region);

        verify(adminService, times(1)).addTournament(anyMap());
        assertEquals("redirect:/admin/admin-tournaments", result);
    }

    // Test for getUsers()
    @Test
    public void testGetUsers() {
        int page = 0;
        int size = 50;
        String username = "testUser";
        Pageable pageable = PageRequest.of(page, size);
        List<User> users = Collections.singletonList(new User());
        Page<User> userPage = new PageImpl<>(users);

        when(userService.searchByUsername(username, pageable)).thenReturn(userPage);

        String result = adminController.getUsers(page, size, username, model);

        verify(userService, times(1)).searchByUsername(username, pageable);
        verify(model, times(1)).addAttribute("users", users);
        verify(model, times(1)).addAttribute("currentPage", page);
        verify(model, times(1)).addAttribute("totalPages", userPage.getTotalPages());
        verify(model, times(1)).addAttribute("username", username);

        assertEquals("admin/user-management", result);
    }

    // Test for getUpdateTournament()
    @Test
    public void testGetUpdateTournament() {
        Long tournamentId = 1L;
        Map<String, Object> tournament = new HashMap<>();
        tournament.put("id", tournamentId);

        when(adminService.getTournamentById(tournamentId)).thenReturn(tournament);

        String result = adminController.getUpdateTournament(tournamentId, model);

        verify(adminService, times(1)).getTournamentById(tournamentId);
        verify(model, times(1)).addAttribute("tournament", tournament);
        assertEquals("admin/updateTournament", result);
    }

    // Test for updateTournament()
    @Test
    public void testUpdateTournament() {
        Long tournamentId = 1L;
        String name = "Updated Tournament";
        String datetime = "2024-01-01T10:00:00";
        int minRank = 1;
        int maxRank = 100;
        String status = "Closed";
        String region = "US";

        String result = adminController.updateTournament(tournamentId, name, datetime, minRank, maxRank, status, region);

        verify(adminService, times(1)).updateTournament(eq(tournamentId), anyMap());
        assertEquals("redirect:/admin/admin-tournaments", result);
    }

    // Test for deleteTournament()
    @Test
    public void testDeleteTournament() {
        Long tournamentId = 1L;

        ResponseEntity<Void> response = adminController.deleteTournament(tournamentId);

        verify(adminService, times(1)).deleteTournament(tournamentId);
        assertEquals(ResponseEntity.noContent().build(), response);
    }

    // Additional tests for startTournament and nextRound can be created in a similar pattern.
}
