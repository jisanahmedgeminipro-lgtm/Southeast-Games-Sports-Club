package bd.edu.seu.gamesclub.dto;

/** Read model for a sponsor. */
public record SponsorResponse(
        Long id,
        String name,
        String logoUrl,
        String website,
        int displayOrder,
        boolean active,
        Long logoMediaId
) {}
