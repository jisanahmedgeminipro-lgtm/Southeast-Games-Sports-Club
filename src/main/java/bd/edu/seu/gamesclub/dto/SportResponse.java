package bd.edu.seu.gamesclub.dto;

/** Read model for a sport. */
public record SportResponse(
        Long id,
        String name,
        String slug,
        String description,
        String iconUrl,
        String imageUrl,
        Long iconMediaId,
        Long imageMediaId,
        int displayOrder,
        boolean active
) {}
