package bd.edu.seu.gamesclub.dto;

/** Read model for a social link. */
public record SocialLinkResponse(
        Long id,
        String platform,
        String url,
        String iconClass,
        int displayOrder,
        boolean active
) {}
