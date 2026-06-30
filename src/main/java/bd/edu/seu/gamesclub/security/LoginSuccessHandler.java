package bd.edu.seu.gamesclub.security;

import bd.edu.seu.gamesclub.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Routes users to the appropriate landing area after login (admins to the admin
 * dashboard, students to the student dashboard) and records the last-login time.
 */
@Slf4j
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Record last-login time in its own transaction so a write hiccup can
        // never break the login flow (the commit happens inside save()).
        try {
            userRepository.findByEmail(authentication.getName()).ifPresent(u -> {
                u.setLastLoginAt(LocalDateTime.now());
                userRepository.save(u);
            });
        } catch (RuntimeException ex) {
            log.warn("Could not update lastLoginAt for {}: {}", authentication.getName(), ex.getMessage());
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        setDefaultTargetUrl(isAdmin ? "/admin/dashboard" : "/student/dashboard");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
