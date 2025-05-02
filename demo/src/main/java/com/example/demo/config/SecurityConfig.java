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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    public SecurityConfig(CustomUserDetailsService uds) {
        this.userDetailsService = uds;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();  // dev only
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration cfg
    ) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // your existing beans…
        http
        .csrf(csrf -> csrf.disable())
        .authenticationProvider(authenticationProvider())
        .authorizeHttpRequests(auth -> auth
            // public & static
            .requestMatchers("/", "/index.html", "/styles.css", "/js/**", "/css/**").permitAll()
            // view pages
            .requestMatchers("/home-page.html").hasAuthority("USER")
            .requestMatchers("/admin-dashboard.html").hasAuthority("ADMIN")
            // APIs…
            .anyRequest().authenticated()
        )
    
        // form login
        .formLogin(form -> form
            .loginPage("/index.html")
            .loginProcessingUrl("/users/login")
            .successHandler((request, response, authentication) -> {
                boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));

                if (isAdmin) {
                    response.sendRedirect("/admin-dashboard.html");
                } else {
                    response.sendRedirect("/home-page.html");
                }
            })
            .permitAll()
        )
 
        // HTTP Basic
        .httpBasic(Customizer.withDefaults())
    
        // <<< add this block >>>
        .exceptionHandling(ex -> ex
            // unauthenticated: send to /index.html
            .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/index.html"))
            // authenticated-but-no-ADMIN: also send to /index.html
            .accessDeniedPage("/index.html")
        );

        return http.build();
    }
}
