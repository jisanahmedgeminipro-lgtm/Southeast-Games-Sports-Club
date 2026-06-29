package bd.edu.seu.gamesclub.dto;

import java.time.LocalDateTime;

/** Read model for an activity-log entry. */
public record ActivityLogResponse(
        Long id,
        String action,
        String entityType,
        Long entityId,
        String description,
        Long actorId,
        LocalDateTime createdAt
) {}
