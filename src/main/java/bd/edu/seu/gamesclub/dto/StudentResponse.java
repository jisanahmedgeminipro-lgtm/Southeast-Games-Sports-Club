package bd.edu.seu.gamesclub.dto;

import java.time.LocalDateTime;

/** Read model for a student profile exposed to the presentation layer. */
public record StudentResponse(
        Long id,
        Long userId,
        String fullName,
        String studentId,
        String email,
        String department,
        String batch,
        String semester,
        String phone,
        String gender,
        boolean active,
        String profilePictureUrl,
        LocalDateTime createdAt
) {}
