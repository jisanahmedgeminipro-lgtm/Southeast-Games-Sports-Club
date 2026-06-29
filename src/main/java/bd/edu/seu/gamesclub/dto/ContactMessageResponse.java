package bd.edu.seu.gamesclub.dto;

import java.time.LocalDateTime;

/** Read model for an admin viewing a contact message. */
public record ContactMessageResponse(
        Long id,
        String name,
        String email,
        String subject,
        String message,
        boolean read,
        LocalDateTime createdAt
) {}
