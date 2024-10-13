package cs204.project.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
public class TournamentIsFullException extends RuntimeException{
  private static final long serialVersionUID = 2L;

  public TournamentIsFullException(Long id) {
        super("Full Player List for tournament " + id);
    }
}
