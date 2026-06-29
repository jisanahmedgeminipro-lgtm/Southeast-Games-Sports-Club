package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.Size;

/** Create/update payload for a hero slide. Display order must remain unique. */
public record HeroSliderRequest(
        @Size(max = 180) String title,
        @Size(max = 220) String subtitle,
        @Size(max = 5000) String description,
        @Size(max = 60) String buttonText,
        @Size(max = 255) String buttonUrl,
        Long backgroundMediaId,
        Integer displayOrder,
        Boolean active
) {}
