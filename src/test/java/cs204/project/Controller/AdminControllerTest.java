package cs204.project.Controller;

// import cs204.project.Controller.AdminController;
import cs204.project.Entity.User;
import cs204.project.Service.AdminService;
import cs204.project.Service.UserDetailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

    @Test
    public void testGetDashboard() {
        String result = adminController.getDashboard();
        assertEquals("redirect:/admin/admin-tournaments", result);
    }

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

    @Test
    public void testAddTournament() throws Exception {
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

    @Test
    public void testGetUpdateTournament() throws Exception {
        Long tournamentId = 1L;
        Map<String, Object> tournament = new HashMap<>();
        tournament.put("id", tournamentId);

        when(adminService.getTournamentById(tournamentId)).thenReturn(tournament);

        String result = adminController.getUpdateTournament(tournamentId, model);

        verify(adminService, times(1)).getTournamentById(tournamentId);
        verify(model, times(1)).addAttribute("tournament", tournament);
        assertEquals("admin/updateTournament", result);
    }

    @Test
    public void testUpdateTournament() throws Exception {
        Long tournamentId = 1L;
        String name = "Updated Tournament";
        String datetime = "2024-01-01T10:00:00";
        int minRank = 1;
        int maxRank = 100;
        String status = "Closed";
        String region = "US";

        String result = adminController.updateTournament(tournamentId, name, datetime, minRank, maxRank, status,
                region);

        verify(adminService, times(1)).updateTournament(eq(tournamentId), anyMap());
        assertEquals("redirect:/admin/admin-tournaments", result);
    }

    @Test
    public void testDeleteTournament() throws Exception {
        Long tournamentId = 1L;

        ResponseEntity<Void> response = adminController.deleteTournament(tournamentId);

        verify(adminService, times(1)).deleteTournament(tournamentId);
        assertEquals(ResponseEntity.noContent().build(), response);
    }

    @Test
    public void testShowAddTournamentPage() {
        String result = adminController.showAddTournamentPage(model);
        assertEquals("admin/addTournament", result);
    }

    @Test
    public void testStartTournament() throws JsonProcessingException {
        Long tournamentId = 1L;
        Map<String, Object> tournamentData = new HashMap<>();
        tournamentData.put("round", 1);
        List<List<Map<String, Object>>> mockGroups = new ArrayList<>();
        List<List<User>> expectedUserGroups = new ArrayList<>();

        when(adminService.getTournamentById(tournamentId)).thenReturn(tournamentData);
        when(adminService.getPlayerList(tournamentData)).thenReturn(new ArrayList<>());
        when(adminService.getFirstRoundGroup(anyMap())).thenReturn(mockGroups);

        String result = adminController.startTournament(tournamentId, model);

        verify(model).addAttribute("round", 1);
        verify(model).addAttribute("tournament", tournamentData);
        verify(model).addAttribute("userGroups", expectedUserGroups);
        assertEquals("admin/startTournament", result);
    }

    @Test
    public void testNextRoundWithContinuingTournament() throws JsonProcessingException {
        Long tournamentId = 1L;
        String groupRanksJson = "[]"; // Provide sample JSON
        Map<String, Object> tournamentData = new HashMap<>();
        tournamentData.put("round", 2);
        tournamentData.put("name", "Test Tournament");
        tournamentData.put("date", "2024-01-01");
        tournamentData.put("status", "In Progress");
        tournamentData.put("region", "US");

        when(adminService.getTournamentById(tournamentId)).thenReturn(tournamentData);
        when(adminService.getNextRoundGroup(anyMap())).thenReturn(new ArrayList<>());

        String result = adminController.nextRound(tournamentId, groupRanksJson, model);

        verify(adminService).updateTournament(eq(tournamentId), anyMap());
        assertEquals("admin/startTournament", result);
    }

    @Test
    public void testNextRoundWithTournamentEnding() throws JsonProcessingException {
        Long tournamentId = 1L;
        String groupRanksJson = "[]";
        Map<String, Object> tournamentData = new HashMap<>();
        tournamentData.put("round", 3); // Will increment to 4
        tournamentData.put("name", "Test Tournament");
        tournamentData.put("date", "2024-01-01");
        tournamentData.put("status", "In Progress");
        tournamentData.put("region", "US");

        when(adminService.getTournamentById(tournamentId)).thenReturn(tournamentData);

        String result = adminController.nextRound(tournamentId, groupRanksJson, model);

        verify(adminService).updateTournament(eq(tournamentId), argThat(map -> "Closed".equals(map.get("status"))));
        assertEquals("redirect:/admin", result);
    }

    @Test
    public void testGetUsersWithoutUsername() {
        int page = 0;
        int size = 50;
        Pageable pageable = PageRequest.of(page, size);
        List<User> users = Collections.singletonList(new User());
        Page<User> userPage = new PageImpl<>(users);

        when(userService.findAllUsers(pageable)).thenReturn(userPage);

        String result = adminController.getUsers(page, size, null, model);

        verify(userService).findAllUsers(pageable);
        verify(model).addAttribute("users", users);
        assertEquals("admin/user-management", result);
    }

    @Test
    public void testCreateUpdatedTournament() {
        Long id = 1L;
        String name = "Test Tournament";
        String datetime = "2024-01-01";
        int minRank = 1;
        int maxRank = 100;
        String status = "Open";
        String region = "US";

        Map<String, Object> result = adminController.createUpdatedTournament(
                id, name, datetime, minRank, maxRank, status, region);

        assertEquals(id, result.get("id"));
        assertEquals(name, result.get("name"));
        assertEquals(datetime, result.get("date"));
        assertArrayEquals(new int[] { minRank, maxRank }, (int[]) result.get("rankRange"));
        assertEquals(status, result.get("status"));
        assertEquals(region, result.get("region"));
    }

    // Additional tests for startTournament and nextRound can be created in a
    // similar pattern.
}
