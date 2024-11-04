package cs204.project;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ProjectApplication {

  public static void main(String[] args) {

    ApplicationContext ctx = SpringApplication.run(ProjectApplication.class, args);
  }
}



















