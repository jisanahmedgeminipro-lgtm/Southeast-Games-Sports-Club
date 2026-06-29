package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Create/update payload for an FAQ. */
public record FaqRequest(
        @NotBlank @Size(max = 255) String question,
        @NotBlank String answer,
        Integer displayOrder,
        Boolean active
) {}
