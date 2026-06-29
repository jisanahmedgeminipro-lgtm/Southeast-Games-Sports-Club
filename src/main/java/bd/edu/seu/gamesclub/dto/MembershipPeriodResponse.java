package bd.edu.seu.gamesclub.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Read model for a membership drive. */
public record MembershipPeriodResponse(
        Long id,
        String title,
        String announcement,
        LocalDate openingDate,
        LocalDate closingDate,
        String status,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        boolean notificationSent
) {}
