package cs204.project.Controller;

import cs204.project.Entity.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Collections;
// import java.util.List;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Hello endpoint should return expected greeting")
    void testHello() {
        String response = homeController.hello();
        assertEquals("Hello, World!", response, "Hello endpoint should return 'Hello, World!'");
    }

    @Test
    @DisplayName("Home endpoint should redirect admin users to admin page")
    void testHomeWithAdminRole() {
        // Arrange
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singleton(adminAuthority));
        when(customUserDetails.getId()).thenReturn(1L);

        // Act
        String viewName = homeController.home(model);

        // Assert
        verify(model).addAttribute("message", "Welcome Admin");
        assertEquals("redirect:/admin", viewName, "Should redirect to admin page");
    }

    @Test
    @DisplayName("Home endpoint should redirect regular users to user page")
    void testHomeWithUserRole() {
        // Arrange
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(userAuthority));
        when(customUserDetails.getId()).thenReturn(1L);

        // Act
        String viewName = homeController.home(model);

        // Assert
        verify(model).addAttribute("message", "Welcome User");
        assertEquals("redirect:/user", viewName, "Should redirect to user page");
    }

    @Test
    @DisplayName("Home endpoint should handle null authentication")
    void testHomeWithNullAuthentication() {
        // Arrange
        Authentication emptyAuth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(emptyAuth);
        when(emptyAuth.getAuthorities()).thenReturn(Collections.emptyList());

        // Act
        String viewName = homeController.home(model);

        // Assert
        verify(model).addAttribute("message", "Welcome User");
        assertEquals("redirect:/user", viewName, "Should redirect to user page");
    }

    @Test
    @DisplayName("Home endpoint should handle unauthenticated users")
    void testHomeWithUnauthenticatedUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        String viewName = homeController.home(model);

        // Assert
        verify(model).addAttribute("message", "Welcome User");
        assertEquals("redirect:/user", viewName, "Should redirect to user page");
    }

    @Test
    @DisplayName("Home endpoint should handle null authorities")
    void testHomeWithNullAuthorities() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        // Act
        String viewName = homeController.home(model);

        // Assert
        verify(model).addAttribute("message", "Welcome User");
        assertEquals("redirect:/user", viewName, "Should redirect to user page");
    }

    @Test
    @DisplayName("Home endpoint should handle custom user details")
    void testHomeWithCustomUserDetails() {
        // Arrange
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(userAuthority));
        when(customUserDetails.getId()).thenReturn(1L);

        // Act
        String viewName = homeController.home(model);

        // Assert
        verify(customUserDetails).getId();
        verify(customUserDetails).getAuthorities();
        assertEquals("redirect:/user", viewName, "Should redirect to user page");
    }
}
