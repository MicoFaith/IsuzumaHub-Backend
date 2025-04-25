package com.isuzumahub.diagnostic.config;

import com.isuzumahub.diagnostic.repository.UserRepository;
import jakarta.servlet.ServletException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            System.out.println("[DEBUG] Attempting to load user by email: " + username);
            return userRepository.findByEmail(username)
                    .map(user -> {
                        System.out.println("[DEBUG] User found: " + user.getEmail() + ", encoded password: " + user.getPassword());
                        return new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPassword(),
                                user.getRoles().stream()
                                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role))
                                        .toList());
                    })
                    .orElseThrow(() -> {
                        System.out.println("[DEBUG] User not found with email: " + username);
                        throw new UsernameNotFoundException("User not found with email: " + username);
                    });
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/signup", "/auth", "/auth/login", "/auth/success", "/auth/error", "/auth/forgot-password").permitAll()
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginProcessingUrl("/auth")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            System.out.println("[DEBUG] Login successful for: " + authentication.getName());
                            response.setStatus(HttpStatus.FOUND.value()); // 302
                            response.setHeader("Location", "/auth/success");
                            response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/; HttpOnly; SameSite=Lax");
                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("[DEBUG] Login failed: " + exception.getMessage());
                            response.setStatus(HttpStatus.FOUND.value()); // 302
                            response.setHeader("Location", "/auth/error");
                        }))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        http.addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(List.of("Location", "Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

class RequestLoggingFilter implements jakarta.servlet.Filter {
    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, jakarta.servlet.FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getMethod().equals("POST") && req.getRequestURI().contains("/auth")) {
            System.out.println("[DEBUG] Request method: " + req.getMethod());
            System.out.println("[DEBUG] Request URI: " + req.getRequestURI());
            System.out.println("[DEBUG] Content-Type: " + req.getContentType());
            System.out.println("[DEBUG] Form parameters - email: " + req.getParameter("email") + ", password: " + req.getParameter("password"));
        }
        chain.doFilter(request, response);
    }
}