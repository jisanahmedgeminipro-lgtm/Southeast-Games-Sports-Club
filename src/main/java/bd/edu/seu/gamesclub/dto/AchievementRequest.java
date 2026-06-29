package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Create/update payload for an achievement. */
public record AchievementRequest(
        @NotBlank @Size(max = 180) String title,
        @NotNull @Min(1990) @Max(2100) Short achievementYear,
        @Size(max = 5000) String description,
        Long imageMediaId,
        Long sportId,
        Integer displayOrder
) {}
