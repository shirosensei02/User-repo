package cs204.project;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		// Start the Spring Boot application
		SpringApplication.run(ProjectApplication.class, args);
    //     Dotenv dotenv = Dotenv.configure().load();
    // System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));


		// No tournament-related operations as we are focusing on login page testing

		// You can add any test logic or bean checks for the login page here if necessary
	}

}
            
            