package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Create/update payload for a news article. */
public record NewsRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank String content,
        Long imageMediaId,
        LocalDate publishDate,
        @Size(max = 20) String status
) {}
