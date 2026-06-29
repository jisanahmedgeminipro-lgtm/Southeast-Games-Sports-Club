package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Create/update payload for a gallery category. */
public record GalleryCategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description
) {}
