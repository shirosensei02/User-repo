// package cs204.project.Controller;

// import cs204.project.Entity.CustomUserDetails;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.security.core.Authentication;
// // import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.ui.Model;
// import org.springframework.web.client.RestTemplate;
// // import org.springframework.web.client.HttpClientErrorException;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.ResponseEntity;
// import org.springframework.core.ParameterizedTypeReference;

// import java.util.*;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// class UserControllerTest {

// @InjectMocks
// private UserController userController;

// @Mock
// private Model model;

// @Mock
// private Authentication authentication;

// @Mock
// private SecurityContext securityContext;

// @Mock
// private CustomUserDetails customUserDetails;

// @Mock
// private RestTemplate restTemplate;

// private final Long TEST_USER_ID = 1L;
// private final String LOCAL_API_BASE_URL =
// "http://localhost:8080/tournaments";

// @BeforeEach
// void setUp() {
// MockitoAnnotations.openMocks(this);
// SecurityContextHolder.setContext(securityContext);

// // Common setup for authentication
// when(securityContext.getAuthentication()).thenReturn(authentication);
// when(authentication.getPrincipal()).thenReturn(customUserDetails);
// when(customUserDetails.getId()).thenReturn(TEST_USER_ID);
// }

// @Nested
// @DisplayName("Home Page Tests")
// class HomePageTests {
// @Test
// @DisplayName("Should return home page with user's tournaments")
// void testGetHomePage_Success() {
// // Arrange
// List<Map<String, Object>> mockTournaments = createMockTournaments();
// ResponseEntity<List<Map<String, Object>>> responseEntity = new
// ResponseEntity<>(mockTournaments,
// HttpStatus.OK);
// when(restTemplate.exchange(
// eq(LOCAL_API_BASE_URL + "/player/" + TEST_USER_ID),
// eq(HttpMethod.GET),
// any(),
// eq(new ParameterizedTypeReference<List<Map<String, Object>>>() {
// })))
// .thenReturn(responseEntity);

// // Act
// String viewName = userController.getHomePage(model);

// // Assert
// verify(model).addAttribute("tournaments", mockTournaments);
// assertEquals("users/home_new", viewName);
// }

// @Test
// @DisplayName("Should handle empty tournament list")
// void testGetHomePage_EmptyTournaments() {
// // Arrange
// ResponseEntity<List<Map<String, Object>>> responseEntity = new
// ResponseEntity<>(Collections.emptyList(),
// HttpStatus.OK);
// when(restTemplate.exchange(
// eq(LOCAL_API_BASE_URL + "/player/" + TEST_USER_ID),
// eq(HttpMethod.GET),
// any(),
// eq(new ParameterizedTypeReference<List<Map<String, Object>>>() {
// })))
// .thenReturn(responseEntity);

// // Act
// String viewName = userController.getHomePage(model);

// // Assert
// verify(model).addAttribute(eq("tournaments"), eq(Collections.emptyList()));
// assertEquals("users/home_new", viewName);
// }
// }

// @Nested
// @DisplayName("Tournament List Tests")
// class TournamentListTests {
// @Test
// @DisplayName("Should return available tournaments for authorized user")
// void testGetTournaments_AuthorizedUser() {
// // Arrange
// List<Map<String, Object>> mockTournaments = createMockTournaments();
// when(authentication.getAuthorities())
// .thenReturn(Collections.singletonList(new
// SimpleGrantedAuthority("ROLE_USER")));
// when(restTemplate.getForObject(
// eq(LOCAL_API_BASE_URL + "/available/" + TEST_USER_ID),
// eq(List.class))).thenReturn(mockTournaments);

// // Act
// String viewName = userController.getTournaments(model);

// // Assert
// verify(model).addAttribute("tournaments", mockTournaments);
// assertEquals("users/tournaments", viewName);
// }

// @Test
// @DisplayName("Should handle API error when fetching tournaments")
// void testGetTournaments_ApiError() {
// // Arrange
// when(restTemplate.getForObject(any(String.class), eq(List.class)))
// .thenThrow(new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE,
// "Service Unavailable"));

// // Act & Assert
// assertThrows(HttpClientErrorException.class, () ->
// userController.getTournaments(model));
// }
// }

// @Nested
// @DisplayName("Tournament Join Tests")
// class TournamentJoinTests {
// @Test
// @DisplayName("Should successfully join tournament")
// void testJoinTournaments_Success() {
// // Arrange
// Long tournamentId = 1L;
// String expectedUrl = LOCAL_API_BASE_URL + "/" + tournamentId + "/player/" +
// TEST_USER_ID;

// // Act
// String viewName = userController.joinTournaments(tournamentId);

// // Assert
// verify(restTemplate).postForObject(eq(expectedUrl), eq(null),
// eq(Void.class));
// assertEquals("redirect:/user", viewName);
// }

// @Test
// @DisplayName("Should handle join tournament failure")
// void testJoinTournaments_Failure() {
// // Arrange
// Long tournamentId = 1L;
// when(restTemplate.postForObject(any(String.class), any(), any()))
// .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad
// Request"));

// // Act & Assert
// assertThrows(HttpClientErrorException.class, () ->
// userController.joinTournaments(tournamentId));
// }
// }

// @Test
// @DisplayName("Should return empty string for profile page")
// void testGetProfilePage() {
// assertEquals("", userController.getProfilePage());
// }

// private List<Map<String, Object>> createMockTournaments() {
// List<Map<String, Object>> tournaments = new ArrayList<>();
// Map<String, Object> tournament = new HashMap<>();
// tournament.put("id", 1L);
// tournament.put("name", "Test Tournament");
// tournament.put("date", "2024-01-01");
// tournament.put("status", "Open");
// tournaments.add(tournament);
// return tournaments;
// }
// }
