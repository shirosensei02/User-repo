package cs204.project.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Error
public class PlayerRankOOBException extends RuntimeException{
  private static final long serialVersionUID = 3L;

  public PlayerRankOOBException(Long id) {
        super("Full Player List for tournament " + id);
    }
}
