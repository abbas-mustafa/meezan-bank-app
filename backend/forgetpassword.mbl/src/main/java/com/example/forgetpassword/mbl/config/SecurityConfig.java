package com.example.forgetpassword.mbl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Main configuration class for Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the HTTP security filter chain, defining public and protected endpoints.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Allow public access to registration, login, and forgot password flows
                .requestMatchers("/api/users/register", "/api/users/login", "/api/account-flow/**").permitAll()
                // All other requests must be authenticated
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults());
        return http.build();
    }

    /**
     * Provides the BCrypt password encoder bean for the application.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines an in-memory user for development and testing purposes.
     * This bean overrides database authentication.
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails devUser = User.builder()
            .username("devuser")
            .password(passwordEncoder.encode("devpass"))
            .roles("USER", "ADMIN")
            .build();

        return new InMemoryUserDetailsManager(devUser);
    }
}