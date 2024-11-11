package cs204.project.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return login page without error message when error parameter is null")
    void testLoginWithNullError() {
        // Act
        String viewName = authenticationController.login(null, model);

        // Assert
        verify(model, never()).addAttribute(eq("errorMessage"), any());
        assertEquals("login_new", viewName, "Should return login_new view name");
    }

    @Test
    @DisplayName("Should return login page with error message when error parameter is true")
    void testLoginWithError() {
        // Act
        String viewName = authenticationController.login("true", model);

        // Assert
        verify(model).addAttribute("errorMessage", "Invalid username or password");
        assertEquals("login_new", viewName, "Should return login_new view name");
    }

    @ParameterizedTest
    @ValueSource(strings = { "true", "false", "any" })
    @DisplayName("Should add error message for any non-null error parameter")
    void testLoginWithDifferentErrorValues(String errorValue) {
        // Act
        String viewName = authenticationController.login(errorValue, model);

        // Assert
        verify(model).addAttribute("errorMessage", "Invalid username or password");
        assertEquals("login_new", viewName, "Should return login_new view name");
    }

    @Test
    @DisplayName("Should not modify model when error parameter is null")
    void testLoginDoesNotModifyModelWithNullError() {
        // Act
        authenticationController.login(null, model);

        // Assert
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    @DisplayName("Should return consistent view name")
    void testLoginViewNameConsistency() {
        // Act
        String viewName1 = authenticationController.login(null, model);
        String viewName2 = authenticationController.login("true", model);

        // Assert
        assertEquals(viewName1, viewName2, "View name should be consistent regardless of error parameter");
        assertEquals("login_new", viewName1, "View name should be login_new");
    }

    @Test
    @DisplayName("Should handle model attribute addition without exceptions")
    void testLoginModelAttributeAddition() {
        // Arrange
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        // Act & Assert
        assertDoesNotThrow(() -> {
            authenticationController.login("true", model);
        }, "Should not throw exception when adding model attribute");
    }
}