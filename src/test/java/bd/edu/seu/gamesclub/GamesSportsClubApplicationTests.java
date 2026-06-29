package bd.edu.seu.gamesclub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: verifies the Spring application context starts successfully
 * under the in-memory {@code test} profile.
 */
@SpringBootTest
@ActiveProfiles("test")
class GamesSportsClubApplicationTests {

    @Test
    void contextLoads() {
        // Intentionally empty - succeeds if the application context bootstraps.
    }
}
