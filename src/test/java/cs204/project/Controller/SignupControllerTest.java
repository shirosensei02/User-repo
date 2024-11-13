package cs204.project.Controller;

import cs204.project.Entity.User;
import cs204.project.Service.UserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SignupControllerTest {

    @InjectMocks
    private SignupController signupController;

    @Mock
    private UserDetailService userService;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("GET /signup tests")
    class SignupFormTests {
        @Test
        @DisplayName("Should return signup form view with empty user")
        void testSignupForm() {
            // Act
            String viewName = signupController.signupForm(model);

            // Assert
            verify(model).addAttribute(eq("user"), argThat(user -> user instanceof User &&
                    ((User) user).getUsername() == null &&
                    ((User) user).getRank() == 100 // Default rank value
            ));
            assertEquals("signup_new", viewName);
        }
    }

    @Nested
    @DisplayName("POST /signup tests")
    class SignupSubmitTests {
        @Test
        @DisplayName("Should register user with default rank and redirect to login")
        void testSignupSubmit_Success() {
            // Arrange
            User user = new User();
            user.setUsername("testUser");
            user.setPassword("testPass123");
            user.setRank(100); // Default rank

            // Act
            String viewName = signupController.signupSubmit(user);

            // Assert
            verify(userService)
                    .registerUser(argThat(registeredUser -> registeredUser.getUsername().equals("testUser") &&
                            registeredUser.getPassword().equals("testPass123") &&
                            registeredUser.getRank() == 100));
            assertEquals("redirect:/login", viewName);
        }

        @Test
        @DisplayName("Should register user with all fields properly set")
        void testSignupSubmit_AllFields() {
            // Arrange
            User user = new User();
            user.setUsername("testUser");
            user.setPassword("testPass123");
            user.setRole("USER");
            user.setRank(100);

            // Act
            String viewName = signupController.signupSubmit(user);

            // Assert
            verify(userService)
                    .registerUser(argThat(registeredUser -> registeredUser.getUsername().equals("testUser") &&
                            registeredUser.getPassword().equals("testPass123") &&
                            registeredUser.getRole().equals("USER") &&
                            registeredUser.getRank() == 100));
            assertEquals("redirect:/login", viewName);
        }

        @Test
        @DisplayName("Should handle user registration with minimum required fields")
        void testSignupSubmit_MinimumFields() {
            // Arrange
            User user = new User();
            user.setUsername("testUser");
            user.setPassword("testPass123");

            // Act
            String viewName = signupController.signupSubmit(user);

            // Assert
            verify(userService)
                    .registerUser(argThat(registeredUser -> registeredUser.getUsername().equals("testUser") &&
                            registeredUser.getPassword().equals("testPass123") &&
                            registeredUser.getRank() == 100 // Default rank should be set
                    ));
            assertEquals("redirect:/login", viewName);
        }
    }

    @Test
    @DisplayName("Should verify userService interaction")
    void testUserServiceInteraction() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass123");

        // Act
        signupController.signupSubmit(user);

        // Assert
        verify(userService, times(1)).registerUser(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Should maintain default rank value during registration")
    void testDefaultRankMaintained() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass123");
        // Not setting rank explicitly to test default value

        // Act
        signupController.signupSubmit(user);

        // Assert
        verify(userService).registerUser(argThat(registeredUser -> registeredUser.getRank() == 100));
    }
}
