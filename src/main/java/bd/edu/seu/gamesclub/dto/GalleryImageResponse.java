package bd.edu.seu.gamesclub.dto;

/** Read model for a gallery image. */
public record GalleryImageResponse(
        Long id,
        Long categoryId,
        String categorySlug,
        String url,
        String caption,
        int displayOrder,
        Long mediaId
) {}
