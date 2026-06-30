package bd.edu.seu.gamesclub.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Central Spring Security configuration: role-based authorization, form login
 * with a role-aware success handler, remember-me, session management, CSRF
 * protection (enabled by default) and a 403 access-denied handler.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Static assets & PWA
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**",
                        "/webjars/**", "/favicon.ico", "/app.webmanifest").permitAll()
                // Public site
                .requestMatchers("/", "/about", "/sports", "/achievements", "/membership",
                        "/committee/**", "/events", "/events/**", "/news", "/news/**",
                        "/gallery", "/gallery/**", "/contact").permitAll()
                // Auth endpoints
                .requestMatchers("/login", "/register", "/verify-otp", "/resend-otp",
                        "/forgot-password", "/reset-password").permitAll()
                // Error pages
                .requestMatchers("/error/**", "/maintenance").permitAll()
                // Role-protected areas
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(loginSuccessHandler)
                .failureHandler((request, response, exception) -> {
                    // Distinguish "not verified / disabled" from bad credentials so
                    // the login page can show an accurate, helpful message.
                    String redirect = (exception instanceof DisabledException)
                            ? "/login?disabled" : "/login?error";
                    response.sendRedirect(request.getContextPath() + redirect);
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("seu-sports-club-remember-me-key")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(14 * 24 * 60 * 60) // 14 days
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(fixation -> fixation.migrateSession())
                .maximumSessions(1)
            )
            .exceptionHandling(ex -> ex
                // Render the branded 403 template via Boot's error handling.
                .accessDeniedHandler((request, response, denied) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN))
            );

        // CSRF protection is enabled by default; Thymeleaf injects the token into
        // every form rendered with th:action.
        return http.build();
    }

    /**
     * Publishes servlet session lifecycle events so Spring Security's concurrent
     * session control ({@code maximumSessions}) can correctly track and evict
     * expired/destroyed sessions from its registry.
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
