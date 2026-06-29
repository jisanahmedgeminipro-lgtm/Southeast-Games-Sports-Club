package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.MembershipApplicationResponse;
import bd.edu.seu.gamesclub.dto.MembershipPeriodResponse;
import bd.edu.seu.gamesclub.entity.MembershipApplication;
import bd.edu.seu.gamesclub.entity.MembershipPeriod;

/** Manual mapper for membership periods and applications. */
public final class MembershipMapper {

    private MembershipMapper() {
    }

    public static MembershipPeriodResponse toPeriodResponse(MembershipPeriod p) {
        if (p == null) {
            return null;
        }
        return new MembershipPeriodResponse(
                p.getId(), p.getTitle(), p.getAnnouncement(),
                p.getOpeningDate(), p.getClosingDate(),
                p.getStatus() != null ? p.getStatus().name() : null,
                p.getOpenedAt(), p.getClosedAt(), p.isNotificationSent()
        );
    }

    /**
     * @param a           the application
     * @param studentName the applicant's full name (resolved from the profile by the service)
     */
    public static MembershipApplicationResponse toApplicationResponse(MembershipApplication a, String studentName) {
        if (a == null) {
            return null;
        }
        return new MembershipApplicationResponse(
                a.getId(),
                a.getPeriod() != null ? a.getPeriod().getId() : null,
                a.getPeriod() != null ? a.getPeriod().getTitle() : null,
                a.getStudent() != null ? a.getStudent().getId() : null,
                studentName,
                a.getStudent() != null ? a.getStudent().getEmail() : null,
                a.getStatus() != null ? a.getStatus().name() : null,
                a.getAppliedAt(),
                a.getReviewedBy() != null ? a.getReviewedBy().getEmail() : null,
                a.getReviewedAt(),
                a.getRemarks()
        );
    }
}
