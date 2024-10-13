package cs204.project.Security;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(
						authz -> authz
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
						.requestMatchers("/signup", "/login").permitAll()
						.anyRequest().authenticated())
						.formLogin(login -> login.loginPage("/login")
								.defaultSuccessUrl("/", true)
								.failureUrl("/login?error=true")
								.permitAll())
						.oauth2Login(oauth -> oauth
								.loginPage("/login")
								.defaultSuccessUrl("/", true)
								.userInfoEndpoint(userInfo -> userInfo
								.userAuthoritiesMapper(this::mapAuthorities))
							)
						.logout(logout -> logout.logoutSuccessUrl("/login?logout=true")
								.permitAll()).build();

	}
    private Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Collection<GrantedAuthority> mappedAuthorities = authorities.stream()
                .map(authority -> new SimpleGrantedAuthority("ROLE_USER"))
                .collect(Collectors.toSet());
        return mappedAuthorities;
    }
	@Bean
     BCryptPasswordEncoder BCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    } 
	
}
