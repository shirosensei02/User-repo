package cs204.project.Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cs204.project.Entity.CustomUserDetails;
import cs204.project.Entity.User;
import cs204.project.Repo.UserRepository;

import java.util.Arrays;

public class UserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailService userDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ReturnsCustomUserDetails() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userDetailService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof CustomUserDetails);
        assertEquals(username, result.getUsername());
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ThrowsException() {
        // Arrange
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailService.loadUserByUsername(username));
    }

    @Test
    void registerUser_Successfully() {
        // Arrange
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("password");
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        userDetailService.registerUser(user);

        // Assert
        verify(userRepository).save(user);
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("USER", user.getRole());
        assertEquals(0, user.getRank());
    }

    @Test
    void registerUser_WithNullUser_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userDetailService.registerUser(null));
    }

    @Test
    void findAllUsers_ReturnsPageOfUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(Arrays.asList(new User(), new User()));
        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<User> result = userDetailService.findAllUsers(pageable);

        // Assert
        assertEquals(expectedPage, result);
        verify(userRepository).findAll(pageable);
    }

    @Test
    void findById_WhenUserExists_ReturnsUser() {
        // Arrange
        Long userId = 1L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userDetailService.findById(userId);

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    void findById_WhenUserDoesNotExist_ReturnsNull() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        User result = userDetailService.findById(userId);

        // Assert
        assertNull(result);
    }

    @Test
    void findByUsername_WhenUserExists_ReturnsUser() {
        // Arrange
        String username = "testUser";
        User expectedUser = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userDetailService.findByUsername(username);

        // Assert
        assertEquals(expectedUser, result);
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ReturnsNull() {
        // Arrange
        String username = "nonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        User result = userDetailService.findByUsername(username);

        // Assert
        assertNull(result);
    }

    @Test
    void save_CallsRepository() {
        // Arrange
        User user = new User();

        // Act
        userDetailService.save(user);

        // Assert
        verify(userRepository).save(user);
    }

    @Test
    void searchByUsername_ReturnsMatchingUsers() {
        // Arrange
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(Arrays.asList(new User(), new User()));
        when(userRepository.findByUsernameContainingIgnoreCase(searchTerm, pageable))
                .thenReturn(expectedPage);

        // Act
        Page<User> result = userDetailService.searchByUsername(searchTerm, pageable);

        // Assert
        assertEquals(expectedPage, result);
        verify(userRepository).findByUsernameContainingIgnoreCase(searchTerm, pageable);
    }
}
