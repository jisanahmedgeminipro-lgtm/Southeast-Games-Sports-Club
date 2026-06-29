package bd.edu.seu.gamesclub.dto;

import java.time.LocalDateTime;

/** Read model for a broadcast campaign history entry. */
public record EmailBroadcastResponse(
        Long id,
        String subject,
        String targetType,
        int recipientCount,
        String status,
        LocalDateTime sentAt,
        LocalDateTime createdAt
) {}
