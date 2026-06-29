package bd.edu.seu.gamesclub.dto;

/** Read model for a gallery category. */
public record GalleryCategoryResponse(
        Long id,
        String name,
        String slug,
        String description,
        long imageCount
) {}
