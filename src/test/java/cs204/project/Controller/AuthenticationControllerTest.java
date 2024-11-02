package cs204.project.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        model = mock(Model.class);
    }

    @Test
    void testLoginWithoutError() {
        // Act
        String viewName = authenticationController.login(null, model);

        // Assert
        verify(model, never()).addAttribute("errorMessage", "Invalid username or password");
        assertEquals("login_new", viewName);
    }

    @Test
    void testLoginWithError() {
        // Act
        String viewName = authenticationController.login("true", model);

        // Assert
        verify(model, times(1)).addAttribute("errorMessage", "Invalid username or password");
        assertEquals("login_new", viewName);
    }
}