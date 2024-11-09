package cs204.project.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

import cs204.project.Entity.CustomUserDetails;
import cs204.project.Entity.User;
import cs204.project.Service.UserDetailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserDetailService userService;

  @Autowired
  public OAuth2LoginSuccessHandler(UserDetailService userService) {
    this.userService = userService;
    setDefaultTargetUrl("/user");
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    User user = userService.findByUsername(email);
    if (user == null) {
      // Create new user
      user = new User();
      user.setUsername(email);
      user.setPassword(""); // OAuth2 users don't need password
      user.setRole("USER");
      user.setRank(0);
      userService.save(user);
    }

    // Create CustomUserDetails from the user
    CustomUserDetails userDetails = new CustomUserDetails(user);
    
    // Create new authentication token with proper authorities
    UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );
    
    // Set the new authentication in the security context
    SecurityContextHolder.getContext().setAuthentication(newAuth);

    // Proceed with the redirect
    super.onAuthenticationSuccess(request, response, authentication);
  }
}