package cs204.project.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should set and get ID correctly")
    void testIdGetterAndSetter() {
        Long testId = 1L;
        user.setId(testId);
        assertEquals(testId, user.getId());
    }

    @Test
    @DisplayName("Should set and get username correctly")
    void testUsernameGetterAndSetter() {
        String testUsername = "testUser";
        user.setUsername(testUsername);
        assertEquals(testUsername, user.getUsername());
    }

    @Test
    @DisplayName("Should set and get password correctly")
    void testPasswordGetterAndSetter() {
        String testPassword = "password123";
        user.setPassword(testPassword);
        assertEquals(testPassword, user.getPassword());
    }

    @Test
    @DisplayName("Should set and get role correctly")
    void testRoleGetterAndSetter() {
        String testRole = "USER";
        user.setRole(testRole);
        assertEquals(testRole, user.getRole());
    }

    @Test
    @DisplayName("Should have default rank of 100")
    void testDefaultRank() {
        assertEquals(100, user.getRank());
    }

    @ParameterizedTest
    @DisplayName("Should set and get rank correctly")
    @ValueSource(ints = { 0, 50, 100, 150, 200 })
    void testRankGetterAndSetter(int testRank) {
        user.setRank(testRank);
        assertEquals(testRank, user.getRank());
    }

    @Test
    @DisplayName("Should create user with all properties")
    void testCompleteUserCreation() {
        // Arrange
        Long testId = 1L;
        String testUsername = "completeUser";
        String testPassword = "securePass123";
        String testRole = "ADMIN";
        int testRank = 150;

        // Act
        user.setId(testId);
        user.setUsername(testUsername);
        user.setPassword(testPassword);
        user.setRole(testRole);
        user.setRank(testRank);

        // Assert
        assertAll("User properties",
                () -> assertEquals(testId, user.getId()),
                () -> assertEquals(testUsername, user.getUsername()),
                () -> assertEquals(testPassword, user.getPassword()),
                () -> assertEquals(testRole, user.getRole()),
                () -> assertEquals(testRank, user.getRank()));
    }

    @Test
    @DisplayName("Should create new user with default rank")
    void testNewUserDefaultValues() {
        User newUser = new User();
        assertEquals(100, newUser.getRank(), "New user should have default rank of 100");
    }

    @Test
    @DisplayName("Should allow null ID for new users")
    void testNullId() {
        assertNull(user.getId(), "New user should have null ID before persistence");
    }
}
