package cs204.project.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {
    private User user;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        // Create a test user
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setRole("USER");

        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void testGetId() {
        assertEquals(1L, customUserDetails.getId());
    }

    @Test
    void testGetUsername() {
        assertEquals("testUser", customUserDetails.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", customUserDetails.getPassword());
    }

    @Test
    void testGetAuthorities() {
        // Check if authorities contain ROLE_USER
        SimpleGrantedAuthority expectedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        assertTrue(customUserDetails.getAuthorities().contains(expectedAuthority));
        assertEquals(1, customUserDetails.getAuthorities().size());
    }

    @Test
    void testAccountNonExpired() {
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void testAccountNonLocked() {
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void testCredentialsNonExpired() {
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void testEnabled() {
        assertTrue(customUserDetails.isEnabled());
    }

    @Test
    void testConstructorWithAdminRole() {
        // Test with ADMIN role
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("adminUser");
        adminUser.setPassword("adminPass");
        adminUser.setRole("ADMIN");

        CustomUserDetails adminDetails = new CustomUserDetails(adminUser);

        SimpleGrantedAuthority expectedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        assertTrue(adminDetails.getAuthorities().contains(expectedAuthority));
    }
}
