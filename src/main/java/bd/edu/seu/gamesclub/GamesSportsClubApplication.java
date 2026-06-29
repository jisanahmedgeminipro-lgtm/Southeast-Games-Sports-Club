package bd.edu.seu.gamesclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application entry point for the Southeast University Games &amp; Sports Club
 * Management System.
 *
 * <p>The following cross-cutting capabilities are enabled at the application level:
 * <ul>
 *     <li>{@code @EnableAsync}     - allows e-mail dispatch (OTP, broadcasts,
 *                                    membership notifications) to run on background
 *                                    threads so HTTP requests are not blocked.</li>
 *     <li>{@code @EnableScheduling} - enables scheduled jobs such as expiring
 *                                     stale OTPs and auto opening/closing of the
 *                                     membership window on configured dates.</li>
 * </ul>
 *
 * @author SEU Games &amp; Sports Club Dev Team
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class GamesSportsClubApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamesSportsClubApplication.class, args);
    }
}
