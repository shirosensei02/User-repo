// package cs204.project.Security;

// // import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import
// org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// // import org.springframework.security.web.SecurityFilterChain;

// import java.util.Collection;
// import java.util.List;
// import static org.junit.jupiter.api.Assertions.*;
// // import static org.mockito.Mockito.*;

// @SpringBootTest
// class SecurityConfigTest {

// @Autowired
// private SecurityConfig securityConfig;

// @MockBean
// private HttpSecurity httpSecurity;

// @Test
// @DisplayName("Should create BCryptPasswordEncoder bean")
// void testBCryptPasswordEncoderBean() {
// // Act
// BCryptPasswordEncoder encoder = securityConfig.BCryptPasswordEncoder();

// // Assert
// assertNotNull(encoder);
// String password = "testPassword";
// String encodedPassword = encoder.encode(password);
// assertTrue(encoder.matches(password, encodedPassword));
// }

// @Test
// @DisplayName("Should map authorities correctly")
// void testMapAuthorities() {
// // Arrange
// List<SimpleGrantedAuthority> authorities = List.of(
// new SimpleGrantedAuthority("GITHUB_USER"),
// new SimpleGrantedAuthority("GOOGLE_USER"));

// // Act
// Collection<? extends GrantedAuthority> mappedAuthorities =
// securityConfig.mapAuthorities(authorities);

// // Assert
// assertEquals(1, mappedAuthorities.size());
// assertTrue(mappedAuthorities.stream()
// .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
// }

// @Test
// @DisplayName("Should encode and verify password correctly")
// void testPasswordEncoding() {
// // Arrange
// BCryptPasswordEncoder encoder = securityConfig.BCryptPasswordEncoder();
// String rawPassword = "myPassword123";

// // Act
// String encodedPassword = encoder.encode(rawPassword);

// // Assert
// assertNotEquals(rawPassword, encodedPassword);
// assertTrue(encoder.matches(rawPassword, encodedPassword));
// assertFalse(encoder.matches("wrongPassword", encodedPassword));
// }

// @Test
// @DisplayName("Should create unique encoded passwords for same input")
// void testUniqueEncodings() {
// // Arrange
// BCryptPasswordEncoder encoder = securityConfig.BCryptPasswordEncoder();
// String password = "testPassword";

// // Act
// String firstEncoding = encoder.encode(password);
// String secondEncoding = encoder.encode(password);

// // Assert
// assertNotEquals(firstEncoding, secondEncoding);
// assertTrue(encoder.matches(password, firstEncoding));
// assertTrue(encoder.matches(password, secondEncoding));
// }
// }
