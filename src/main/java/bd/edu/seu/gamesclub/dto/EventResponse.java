package bd.edu.seu.gamesclub.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Read model for an event. */
public record EventResponse(
        Long id,
        String title,
        String slug,
        String description,
        String bannerUrl,
        String venue,
        LocalDate eventDate,
        LocalTime eventTime,
        LocalDateTime registrationDeadline,
        String status,
        Long sportId,
        String sportName,
        Long bannerMediaId
) {}
