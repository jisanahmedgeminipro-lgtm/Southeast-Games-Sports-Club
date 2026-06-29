package bd.edu.seu.gamesclub.dto;

/** Read model for a stored media asset. */
public record MediaAssetResponse(
        Long id,
        String url,
        String altText,
        String mimeType,
        Long fileSize,
        Integer width,
        Integer height,
        String mediaType
) {}
