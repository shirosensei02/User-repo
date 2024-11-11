package cs204.project.Util;

import cs204.project.Entity.User;
import cs204.project.Repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultAdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private DefaultAdminInitializer defaultAdminInitializer;

    @Test
    void whenAdminDoesNotExist_thenCreateAdmin() throws Exception {
        // Arrange
        when(userRepository.findByRole("ADMIN")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");

        // Act
        defaultAdminInitializer.run();

        // Assert
        verify(userRepository).findByRole("ADMIN");
        verify(passwordEncoder).encode("admin123");
        verify(userRepository).save(argThat(user -> user.getUsername().equals("admin") &&
                user.getRole().equals("ADMIN") &&
                user.getPassword().equals("encodedPassword")));
    }

    @Test
    void whenAdminExists_thenDoNotCreateAdmin() throws Exception {
        // Arrange
        User existingAdmin = new User();
        existingAdmin.setUsername("admin");
        existingAdmin.setRole("ADMIN");
        when(userRepository.findByRole("ADMIN")).thenReturn(Optional.of(existingAdmin));

        // Act
        defaultAdminInitializer.run();

        // Assert
        verify(userRepository).findByRole("ADMIN");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
