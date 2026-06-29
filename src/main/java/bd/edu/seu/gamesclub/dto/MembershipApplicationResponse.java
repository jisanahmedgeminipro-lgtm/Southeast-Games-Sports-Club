package bd.edu.seu.gamesclub.dto;

import java.time.LocalDateTime;

/** Read model for a membership application. */
public record MembershipApplicationResponse(
        Long id,
        Long periodId,
        String periodTitle,
        Long studentUserId,
        String studentName,
        String studentEmail,
        String status,
        LocalDateTime appliedAt,
        String reviewedBy,
        LocalDateTime reviewedAt,
        String remarks
) {}
