package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.MembershipApplicationResponse;
import bd.edu.seu.gamesclub.dto.MembershipPeriodRequest;
import bd.edu.seu.gamesclub.dto.MembershipPeriodResponse;
import java.util.List;

/**
 * Membership lifecycle and applications.
 *
 * <p>Business rules: membership is never open all year; applications are only
 * accepted while a period is OPEN; a student may apply at most once per period;
 * opening a drive notifies every registered student by email.
 */
public interface MembershipService {

    /** Create a new (DRAFT) membership drive. */
    MembershipPeriodResponse createPeriod(MembershipPeriodRequest request);

    /** Update an existing membership drive's details/announcement/dates. */
    MembershipPeriodResponse updatePeriod(Long id, MembershipPeriodRequest request);

    /** Open a drive and email all registered students (once). */
    void open(Long periodId, String adminEmail);

    /** Close a drive. */
    void close(Long periodId, String adminEmail);

    /** All drives, newest first. */
    List<MembershipPeriodResponse> getAllPeriods();

    /** The active (OPEN) period, or {@code null} if membership is closed. */
    MembershipPeriodResponse getActivePeriod();

    /** The most recent period (for the public announcement), or {@code null}. */
    MembershipPeriodResponse getCurrentPeriod();

    /** Whether membership is currently open. */
    boolean isOpen();

    /** Student action: apply for membership in the active period. */
    MembershipApplicationResponse apply(String studentEmail);

    /** The authenticated student's application in the active period, or {@code null}. */
    MembershipApplicationResponse getMyApplication(String studentEmail);

    /** Admin: list applications, optionally filtered by status (blank/null = all). */
    List<MembershipApplicationResponse> getApplications(String status);

    /** Admin: approve or reject an application. */
    void review(Long applicationId, boolean approve, String remarks, String adminEmail);

    /** Scheduler: open DRAFT drives whose opening date has arrived (sends notifications). */
    int openDuePeriods();

    /** Scheduler: close OPEN drives whose closing date has passed. */
    int closeExpiredPeriods();
}
