package cs204.project.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import cs204.project.model.player.*;
// import cs204.project.model.player.Player.userRole;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        Optional<Player> admin = playerRepository.findByUsername("ADMIN");
        if (!(admin.isPresent())) {
            // Create and save the default admin user
            // Player adminUser = new Player("admin", passwordEncoder.encode("admin123"), userRole.ADMIN);
            Player adminUser = new Player("ADMIN", passwordEncoder.encode("admin123"), "ADMIN");
            // Player adminUser = new Player();
            // adminUser.setUsername("admin");
            // adminUser.setPassword(passwordEncoder.encode("admin123")); // Set a default password

            // adminUser.setRole(userRole.ADMIN);
            // Debugging info before saving
            System.out.println("Creating user: " + adminUser.getUsername() + " with role: " + adminUser.getRole());

            // Save the user
            playerRepository.save(adminUser);
            System.out.println("Default admin user created");
        } else {
            System.out.println("Admin user already exists.");
        }
    }

}
