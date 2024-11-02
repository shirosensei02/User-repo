package cs204.project;

import cs204.project.Controller.AuthenticationController;
import cs204.project.Controller.AdminController;
import cs204.project.Entity.User;
import cs204.project.Service.AdminService;
import cs204.project.Service.UserDetailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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