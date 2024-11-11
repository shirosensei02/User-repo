package cs204.project.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.fasterxml.jackson.core.JsonProcessingException;

@ControllerAdvice
public class GlobalExceptionHandler {

  // @ExceptionHandler(MatchNotFoundException.class)
  // public ResponseEntity<?>
  // handleMatchHistoryNotFoundException(MatchNotFoundException ex, WebRequest
  // request) {
  // // return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  // }

  // @ExceptionHandler(PlayerNotFoundException.class)
  // public ResponseEntity<?>
  // handlePlayerHistoryNotFoundException(PlayerNotFoundException ex, WebRequest
  // request) {
  // return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  // }

  // @ExceptionHandler(TournamentHistoryNotFoundException.class)
  // public ResponseEntity<?>
  // handleTournamentHistoryNotFoundException(TournamentHistoryNotFoundException
  // ex, WebRequest request) {
  // return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  // }

  // Handle 404 Not Found errors thrown by RestTemplate
  @ExceptionHandler(HttpClientErrorException.NotFound.class)
  public String handleNotFound(HttpClientErrorException.NotFound ex, Model model) {
    // Get the error message from the backend response body
    String errorMessage = ex.getResponseBodyAsString();
    model.addAttribute("errorMessage", "Error: " + errorMessage);
    return "error"; // Return the error page
  }

  // Handle other RestTemplate exceptions
  @ExceptionHandler(RestClientException.class)
  public String handleRestClientErrors(RestClientException ex, Model model) {
    model.addAttribute("errorMessage", "An error occurred while communicating with the service: " + ex.getMessage());
    return "error"; // Return the error page
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public String handleClientError(HttpClientErrorException e, Model model) {
    model.addAttribute("errorMessage", "Client error: " + e.getMessage());
    return "error";
  }

  @ExceptionHandler(HttpServerErrorException.class)
  public String handleServerError(HttpServerErrorException e, Model model) {
    model.addAttribute("errorMessage", "Server error: " + e.getMessage());
    return "error";
  }

  @ExceptionHandler(Exception.class)
  public String handleOtherErrors(Exception e, Model model) {
    model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
    return "error";
  }

  @ExceptionHandler(JsonProcessingException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleDataProcessingError(JsonProcessingException e, Model model) {
    model.addAttribute("Data processing error", e.getMessage());
    return "error";
  }
  // You can add more exception handlers for other exceptions
}
