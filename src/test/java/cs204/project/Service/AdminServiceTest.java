package cs204.project.Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import cs204.project.Controller.Player;

public class AdminServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserDetailServiceTest userService;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTournaments_ShouldReturnTournamentsList() {
        // Arrange
        List<Map<String, Object>> expectedTournaments = new ArrayList<>();
        when(restTemplate.getForObject("http://localhost:8080/tournaments", List.class))
                .thenReturn(expectedTournaments);

        // Act
        List<Map<String, Object>> result = adminService.getAllTournaments();

        // Assert
        assertEquals(expectedTournaments, result);
        verify(restTemplate).getForObject("http://localhost:8080/tournaments", List.class);
    }

    @Test
    void getTournamentById_ShouldReturnTournament() {
        // Arrange
        Long id = 1L;
        Map<String, Object> expectedTournament = new HashMap<>();
        when(restTemplate.getForObject("http://localhost:8080/tournaments/" + id, Map.class))
                .thenReturn(expectedTournament);

        // Act
        Map<String, Object> result = adminService.getTournamentById(id);

        // Assert
        assertEquals(expectedTournament, result);
    }

    // @Test
    // void getPlayerList_ShouldReturnPlayersList() {
    // // Arrange
    // Map<String, Object> tournamentData = new HashMap<>();
    // List<Long> playerListRaw = Arrays.asList(1L, 2L);
    // tournamentData.put("playerList", playerListRaw);

    // when(userService.findById(1L)).thenReturn(createMockUser(1L, 1000));
    // when(userService.findById(2L)).thenReturn(createMockUser(2L, 2000));

    // // Act
    // List<Player> result = adminService.getPlayerList(tournamentData);

    // // Assert
    // assertEquals(2, result.size());
    // assertEquals(1L, result.get(0).getId());
    // assertEquals(1000, result.get(0).getRank());
    // assertEquals(2L, result.get(1).getId());
    // assertEquals(2000, result.get(1).getRank());
    // }

    // // Helper method to create mock user
    // private UserDetailServiceTest.User createMockUser(Long id, int rank) {
    // UserDetailServiceTest.User user = mock(UserDetailServiceTest.User.class);
    // when(user.getRank()).thenReturn(rank);
    // return user;
    // }
}
