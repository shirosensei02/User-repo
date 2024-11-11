// package cs204.project.Repo;

// import cs204.project.Entity.User;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import static org.assertj.core.api.Assertions.assertThat;

// import java.util.List;
// import java.util.Optional;

// @DataJpaTest
// class UserRepositoryTest {

// @Autowired
// private UserRepository userRepository;

// private User testUser;

// @BeforeEach
// void setUp() {
// userRepository.deleteAll();

// testUser = new User();
// testUser.setUsername("testUser");
// testUser.setPassword("password123");
// testUser.setRole("USER");
// testUser.setRank(100);

// userRepository.save(testUser);
// }

// @Test
// @DisplayName("Should find user by username")
// void testFindByUsername() {
// // Act
// Optional<User> found = userRepository.findByUsername("testUser");

// // Assert
// assertThat(found).isPresent();
// assertThat(found.get().getUsername()).isEqualTo(testUser.getUsername());
// }

// @Test
// @DisplayName("Should return empty when username not found")
// void testFindByUsernameNotFound() {
// // Act
// Optional<User> notFound = userRepository.findByUsername("nonexistent");

// // Assert
// assertThat(notFound).isEmpty();
// }

// @Test
// @DisplayName("Should find user by role")
// void testFindByRole() {
// // Act
// Optional<User> found = userRepository.findByRole("USER");

// // Assert
// assertThat(found).isPresent();
// assertThat(found.get().getRole()).isEqualTo(testUser.getRole());
// }

// @Test
// @DisplayName("Should find all users")
// void testFindAll() {
// // Arrange
// User secondUser = new User();
// secondUser.setUsername("testUser2");
// secondUser.setPassword("password456");
// secondUser.setRole("ADMIN");
// userRepository.save(secondUser);

// // Act
// List<User> users = userRepository.findAll();

// // Assert
// assertThat(users).hasSize(2);
// assertThat(users).extracting(User::getUsername)
// .containsExactlyInAnyOrder("testUser", "testUser2");
// }

// @Test
// @DisplayName("Should find users by username containing string (case
// insensitive)")
// void testFindByUsernameContainingIgnoreCase() {
// // Arrange
// User user2 = new User();
// user2.setUsername("TestUser2");
// userRepository.save(user2);

// User user3 = new User();
// user3.setUsername("AnotherUser");
// userRepository.save(user3);

// Pageable pageable = PageRequest.of(0, 10);

// // Act
// Page<User> foundUsers =
// userRepository.findByUsernameContainingIgnoreCase("test", pageable);

// // Assert
// assertThat(foundUsers.getContent()).hasSize(2);
// assertThat(foundUsers.getContent()).extracting(User::getUsername)
// .containsExactlyInAnyOrder("testUser", "TestUser2");
// }

// @Test
// @DisplayName("Should handle pagination correctly")
// void testPagination() {
// // Arrange
// for (int i = 1; i <= 15; i++) {
// User user = new User();
// user.setUsername("testUser" + i);
// userRepository.save(user);
// }

// Pageable firstPage = PageRequest.of(0, 5);
// Pageable secondPage = PageRequest.of(1, 5);

// // Act
// Page<User> firstPageResult =
// userRepository.findByUsernameContainingIgnoreCase("test", firstPage);
// Page<User> secondPageResult =
// userRepository.findByUsernameContainingIgnoreCase("test", secondPage);

// // Assert
// assertThat(firstPageResult.getContent()).hasSize(5);
// assertThat(secondPageResult.getContent()).hasSize(5);
// assertThat(firstPageResult.getTotalElements()).isEqualTo(16); // 15 +
// original testUser
// assertThat(firstPageResult.getTotalPages()).isEqualTo(4);
// }

// @Test
// @DisplayName("Should save and retrieve user correctly")
// void testSaveAndRetrieve() {
// // Arrange
// User newUser = new User();
// newUser.setUsername("newUser");
// newUser.setPassword("newPassword");
// newUser.setRole("USER");

// // Act
// User saved = userRepository.save(newUser);
// Optional<User> found = userRepository.findById(saved.getId());

// // Assert
// assertThat(found).isPresent();
// assertThat(found.get().getUsername()).isEqualTo("newUser");
// }
// }
