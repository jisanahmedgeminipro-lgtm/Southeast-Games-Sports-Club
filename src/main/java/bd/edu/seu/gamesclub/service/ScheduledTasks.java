package bd.edu.seu.gamesclub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Background maintenance jobs. Scheduling is enabled application-wide via
 * {@code @EnableScheduling} on the main application class.
 *
 * <ul>
 *     <li><b>OTP cleanup / expired OTP removal</b> - hourly.</li>
 *     <li><b>Membership opening notifications</b> - daily, opens DRAFT drives whose
 *         opening date has arrived and emails all students.</li>
 *     <li><b>Membership closing</b> - daily, closes OPEN drives past their closing date.</li>
 * </ul>
 */
@Slf4j
@Component
public class ScheduledTasks {

    private final OtpService otpService;
    private final MembershipService membershipService;

    public ScheduledTasks(OtpService otpService, MembershipService membershipService) {
        this.otpService = otpService;
        this.membershipService = membershipService;
    }

    /** Remove expired OTP tokens every hour. */
    @Scheduled(cron = "0 0 * * * *")
    public void purgeExpiredOtps() {
        int removed = otpService.purgeExpired();
        if (removed > 0) {
            log.info("Scheduled OTP cleanup removed {} expired token(s).", removed);
        }
    }

    /** Open due membership drives (and send notifications) every day at 00:05. */
    @Scheduled(cron = "0 5 0 * * *")
    public void openDueMembershipPeriods() {
        int opened = membershipService.openDuePeriods();
        if (opened > 0) {
            log.info("Scheduled job opened {} membership period(s) and sent notifications.", opened);
        }
    }

    /** Close membership drives past their closing date every day at 23:55. */
    @Scheduled(cron = "0 55 23 * * *")
    public void closeExpiredMembershipPeriods() {
        int closed = membershipService.closeExpiredPeriods();
        if (closed > 0) {
            log.info("Scheduled job closed {} expired membership period(s).", closed);
        }
    }
}
