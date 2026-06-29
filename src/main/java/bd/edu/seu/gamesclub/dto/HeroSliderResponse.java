package bd.edu.seu.gamesclub.dto;

/** Read model for a hero slide. */
public record HeroSliderResponse(
        Long id,
        String title,
        String subtitle,
        String description,
        String buttonText,
        String buttonUrl,
        String backgroundUrl,
        int displayOrder,
        boolean active,
        Long backgroundMediaId
) {}
