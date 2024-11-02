// package cs204.project.Controller;

// import cs204.project.Entity.CustomUserDetails;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.ui.Model;

// import java.util.ArrayList;
// import java.util.List;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.assertEquals;

// class HomeControllerTest {

//     @InjectMocks
//     private HomeController homeController;

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
//         MockitoAnnotations.openMocks(this);
//         SecurityContextHolder.setContext(securityContext); // Set the security context
//     }

//     @Test
//     void testHello() {
//         // Act
//         String response = homeController.hello();

//         // Assert
//         assertEquals("Hello, World!", response);
//     }

//     @Test
//     void testHomeWithAdminRole() {
//         // Arrange
//         mockAuthentication(true, customUserDetails, List.of(() -> "ROLE_ADMIN"));

//         // Act
//         String viewName = homeController.home(model);

//         // Assert
//         verify(model).addAttribute("message", "Welcome Admin");
//         assertEquals("redirect:/admin", viewName);
//     }

//     @Test
//     void testHomeWithUserRole() {
//         // Arrange
//         mockAuthentication(true, customUserDetails, List.of(() -> "ROLE_USER"));

//         // Act
//         String viewName = homeController.home(model);

//         // Assert
//         verify(model).addAttribute("message", "Welcome User");
//         assertEquals("redirect:/user", viewName);
//     }

//     @Test
//     void testHomeWithoutAuthentication() {
//         // Arrange
//         when(securityContext.getAuthentication()).thenReturn(null); // No authentication

//         // Act
//         String viewName = homeController.home(model);

//         // Assert
//         verify(model).addAttribute("message", "Welcome User");
//         assertEquals("redirect:/user", viewName);
//     }

//     private void mockAuthentication(boolean isAuthenticated, CustomUserDetails userDetails, List<GrantedAuthority> authorities) {
//         when(securityContext.getAuthentication()).thenReturn(authentication);
//         when(authentication.isAuthenticated()).thenReturn(isAuthenticated);
//         when(authentication.getPrincipal()).thenReturn(userDetails);
//         when(authentication.getAuthorities()).thenReturn(authorities); // Mock the authorities
//     }
// }
