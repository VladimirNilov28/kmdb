package org.example.movesapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Super basic Spring Security configuration.
 * <p>
 * Sets up HTTP Basic authentication, disables CSRF, and registers a default in-memory admin user.
 */
@Configuration
class SecurityConfig {

    /*
     * Configures a basic security filter chain with HTTP Basic authentication.
     * All requests must be authenticated. CSRF is disabled for simplicity.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/**")
                        .authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // Provides a BCrypt password encoder for encoding user passwords.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Defines an in-memory admin user for development/testing purposes only.
     * Username: "admin", Password: "admin"
     */
    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails admin = users
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles()
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}