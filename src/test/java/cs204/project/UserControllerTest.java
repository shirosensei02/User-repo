// package cs204.project;

// import cs204.project.Controller.UserController;
// import cs204.project.Entity.CustomUserDetails;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.ui.Model;
// import org.springframework.web.client.RestTemplate;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.assertEquals;

// class UserControllerTest {

//     @InjectMocks
//     private UserController userController;

//     @Mock
//     private RestTemplate restTemplate;

//     @Mock
//     private Model model;

//     @Mock
//     private Authentication authentication;

//     @Mock
//     private SecurityContext securityContext;

//     @Mock
//     private CustomUserDetails customUserDetails;

//     @BeforeEach
//     void setUp() {
//         // Initialize mocks
//         MockitoAnnotations.openMocks(this);
//         // Set the SecurityContextHolder with the mock security context
//         SecurityContextHolder.setContext(securityContext);
//     }

//     @Test
//     void testGetHomePage() {
//         // Arrange
//         Long userId = 1L;
//         List<Map<String, Object>> mockTournaments = new ArrayList<>();
//         Map<String, Object> tournament = new HashMap<>();
//         tournament.put("name", "Tournament 1");
//         mockTournaments.add(tournament);

//         // Mock the security context and the RestTemplate behavior
//         when(securityContext.getAuthentication()).thenReturn(authentication);
//         when(authentication.getPrincipal()).thenReturn(customUserDetails);
//         when(customUserDetails.getId()).thenReturn(userId);
//         when(restTemplate.getForObject(any(String.class), eq(List.class))).thenReturn(mockTournaments);

//         // Act
//         String viewName = userController.getHomePage(model);

//         // Assert
//         verify(model).addAttribute("tournaments", mockTournaments);
//         assertEquals("users/home_new", viewName);
//     }

//     @Test
//     void testGetTournaments() {
//         // Arrange
//         Long userId = 1L;
//         List<Map<String, Object>> mockTournaments = new ArrayList<>();
//         Map<String, Object> tournament = new HashMap<>();
//         tournament.put("name", "Tournament 2");
//         mockTournaments.add(tournament);

//         // Mock the security context and the RestTemplate behavior
//         when(securityContext.getAuthentication()).thenReturn(authentication);
//         when(authentication.getPrincipal()).thenReturn(customUserDetails);
//         when(customUserDetails.getId()).thenReturn(userId);
//         when(restTemplate.getForObject(any(String.class), eq(List.class))).thenReturn(mockTournaments);

//         // Act
//         String viewName = userController.getTournaments(model);

//         // Assert
//         verify(model).addAttribute("tournaments", mockTournaments);
//         assertEquals("users/tournaments", viewName);
//     }

//     @Test
//     void testJoinTournaments() {
//         // Arrange
//         Long tournamentId = 1L;
//         Long userId = 1L;

//         // Mock the security context
//         when(securityContext.getAuthentication()).thenReturn(authentication);
//         when(authentication.getPrincipal()).thenReturn(customUserDetails);
//         when(customUserDetails.getId()).thenReturn(userId);

//         // Act
//         String viewName = userController.joinTournaments(tournamentId);

//         // Assert
//         verify(restTemplate).postForObject(any(String.class), any(), eq(Void.class));
//         assertEquals("redirect:/user", viewName);
//     }

//     @Test
//     void testGetProfilePage() {
//         // Act
//         String viewName = userController.getProfilePage();

//         // Assert
//         assertEquals("", viewName); // Currently, this method returns an empty string
//     }
// }
