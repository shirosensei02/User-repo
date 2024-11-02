package cs204.project.Controller;

import cs204.project.Entity.User;
import cs204.project.Service.UserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    // @Test
    // void testSignupForm() {
    //     // Arrange
    //     User user = new User(); // Create a new User object to be added to the model
    //     when(model.addAttribute("user", user)).thenReturn(model); // Mocking model behavior

    //     // Act
    //     String viewName = signupController.signupForm(model);

    //     // Assert
    //     verify(model).addAttribute("user", user); // Check if user was added to the model
    //     assertEquals("signup_new", viewName); // Ensure the view name is correct
    // }

    @Test
    void testSignupSubmit() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        // Act
        String viewName = signupController.signupSubmit(user);

        // Assert
        verify(userService).registerUser(user); // Ensure the registerUser method was called
        assertEquals("redirect:/login", viewName); // Ensure the redirection is correct
    }
}
