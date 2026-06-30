package bd.edu.seu.gamesclub.config;

import bd.edu.seu.gamesclub.entity.User;
import bd.edu.seu.gamesclub.entity.enums.Role;
import bd.edu.seu.gamesclub.repository.UserRepository;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Optional one-time admin bootstrap.
 *
 * <p>When {@code app.admin.email} and {@code app.admin.password} are provided
 * (typically via the {@code ADMIN_EMAIL} / {@code ADMIN_PASSWORD} environment
 * variables), an enabled, email-verified {@code ROLE_ADMIN} account is created
 * at startup <em>if it does not already exist</em>. The password is BCrypt
 * encoded. If the properties are blank, this does nothing - admins can still be
 * created directly in the database.
 */
@Slf4j
@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.email:}") String adminEmail,
                            @Value("${app.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return; // not configured - skip
        }
        String email = adminEmail.trim().toLowerCase(Locale.ENGLISH);
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("Admin bootstrap: account '{}' already exists - skipping.", email);
            return;
        }
        User admin = new User();
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setEmailVerified(true);
        admin.setEnabled(true);
        userRepository.save(admin);
        log.info("Admin bootstrap: created admin account '{}'.", email);
    }
}
