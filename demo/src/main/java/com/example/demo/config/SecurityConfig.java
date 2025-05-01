package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity  // Enables @PreAuthorize, etc.
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Use plain text password encoder (NOT for production!)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // Authentication provider using custom user details service
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Provide the AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    // Main security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for Postman/curl use
            .csrf(csrf -> csrf.disable())

            // Use our custom authentication provider
            .authenticationProvider(authenticationProvider())

            // Define URL access rules
            .authorizeHttpRequests(auth -> auth

                // Public login endpoint
                .requestMatchers("/users/login").permitAll()

                // USER-only endpoints
                .requestMatchers("/messages/send").hasAuthority("USER")
                .requestMatchers("/complaints/upload").hasAuthority("USER")
                .requestMatchers("/accounts/transfer").hasAuthority("USER")

                // ADMIN-only endpoints
                .requestMatchers("/users/**").hasAuthority("ADMIN")
                .requestMatchers("/messages").hasAuthority("ADMIN")
                .requestMatchers("/complaints").hasAuthority("ADMIN")
                .requestMatchers("/accounts/**").hasAuthority("ADMIN")
                .requestMatchers("/transactions").hasAuthority("ADMIN")

                // Any other request must be authenticated
                .anyRequest().authenticated()
            )

            // Use HTTP Basic auth (suitable for testing with Postman)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
