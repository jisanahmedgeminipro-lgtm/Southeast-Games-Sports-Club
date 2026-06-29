package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Create/update payload for a sponsor. */
public record SponsorRequest(
        @NotBlank @Size(max = 120) String name,
        Long logoMediaId,
        @Size(max = 255) String website,
        Integer displayOrder,
        Boolean active
) {}
