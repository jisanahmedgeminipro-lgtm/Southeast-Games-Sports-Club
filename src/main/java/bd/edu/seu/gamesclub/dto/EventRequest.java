package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Create/update payload for an event. */
public record EventRequest(
        @NotBlank @Size(max = 160) String title,
        @Size(max = 10000) String description,
        Long bannerMediaId,
        @Size(max = 160) String venue,
        @NotNull LocalDate eventDate,
        LocalTime eventTime,
        LocalDateTime registrationDeadline,
        @Size(max = 20) String status,
        Long sportId
) {}
