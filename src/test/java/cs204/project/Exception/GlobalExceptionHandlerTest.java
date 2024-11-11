package cs204.project.Exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler();
    }

    // @Test
    // @DisplayName("Should handle 404 Not Found errors")
    // void testHandleNotFound() {
    // // Arrange
    // String errorBody = "Resource not found";
    // HttpClientErrorException.NotFound ex =
    // HttpClientErrorException.NotFound.create(
    // HttpStatus.NOT_FOUND, "Not Found", errorBody.getBytes(), null);

    // // Act
    // String viewName = exceptionHandler.handleNotFound(ex, model);

    // // Assert
    // verify(model).addAttribute("errorMessage", "Error: " + errorBody);
    // assertEquals("error", viewName);
    // }

    @Test
    @DisplayName("Should handle RestClientException")
    void testHandleRestClientErrors() {
        // Arrange
        String errorMessage = "Connection refused";
        RestClientException ex = new RestClientException(errorMessage);

        // Act
        String viewName = exceptionHandler.handleRestClientErrors(ex, model);

        // Assert
        verify(model).addAttribute("errorMessage",
                "An error occurred while communicating with the service: " + errorMessage);
        assertEquals("error", viewName);
    }

    @Test
    @DisplayName("Should handle HttpClientErrorException")
    void testHandleClientError() {
        // Arrange
        HttpClientErrorException ex = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, "Bad Request");

        // Act
        String viewName = exceptionHandler.handleClientError(ex, model);

        // Assert
        verify(model).addAttribute("errorMessage", "Client error: " + ex.getMessage());
        assertEquals("error", viewName);
    }

    @Test
    @DisplayName("Should handle HttpServerErrorException")
    void testHandleServerError() {
        // Arrange
        HttpServerErrorException ex = new HttpServerErrorException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");

        // Act
        String viewName = exceptionHandler.handleServerError(ex, model);

        // Assert
        verify(model).addAttribute("errorMessage", "Server error: " + ex.getMessage());
        assertEquals("error", viewName);
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void testHandleOtherErrors() {
        // Arrange
        Exception ex = new Exception("Unexpected error");

        // Act
        String viewName = exceptionHandler.handleOtherErrors(ex, model);

        // Assert
        verify(model).addAttribute("errorMessage",
                "An unexpected error occurred: " + ex.getMessage());
        assertEquals("error", viewName);
    }

    @Test
    @DisplayName("Should handle JsonProcessingException")
    void testHandleDataProcessingError() {
        // Arrange
        JsonProcessingException ex = mock(JsonProcessingException.class);
        when(ex.getMessage()).thenReturn("Invalid JSON");

        // Act
        String viewName = exceptionHandler.handleDataProcessingError(ex, model);

        // Assert
        verify(model).addAttribute("Data processing error", ex.getMessage());
        assertEquals("error", viewName);
    }

    @Test
    @DisplayName("Should handle HttpClientErrorException with different status codes")
    void testHandleClientErrorWithDifferentStatusCodes() {
        // Test different HTTP status codes
        HttpStatus[] statuses = {
                HttpStatus.BAD_REQUEST,
                HttpStatus.UNAUTHORIZED,
                HttpStatus.FORBIDDEN,
                HttpStatus.METHOD_NOT_ALLOWED
        };

        for (HttpStatus status : statuses) {
            // Arrange
            HttpClientErrorException ex = new HttpClientErrorException(status, status.getReasonPhrase());

            // Act
            String viewName = exceptionHandler.handleClientError(ex, model);

            // Assert
            verify(model).addAttribute("errorMessage", "Client error: " +
                    status.value() + " " + status.getReasonPhrase());
            assertEquals("error", viewName);
        }
    }
}
