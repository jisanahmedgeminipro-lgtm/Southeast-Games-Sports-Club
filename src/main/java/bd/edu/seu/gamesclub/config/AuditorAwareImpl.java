package bd.edu.seu.gamesclub.config;

import bd.edu.seu.gamesclub.repository.UserRepository;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Supplies the id of the currently authenticated {@link bd.edu.seu.gamesclub.entity.User}
 * so Spring Data JPA auditing can populate {@code created_by} / {@code updated_by}.
 *
 * <p>Returns an empty optional for anonymous/system actions (e.g. public contact
 * submissions, scheduled jobs), in which case the audit columns stay null.
 */
@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<Long> {

    private final UserRepository userRepository;

    public AuditorAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.empty();
            }
            return userRepository.findByEmail(auth.getName()).map(u -> u.getId());
        } catch (RuntimeException ex) {
            // Never let auditor resolution break a persist/flush.
            return Optional.empty();
        }
    }
}
