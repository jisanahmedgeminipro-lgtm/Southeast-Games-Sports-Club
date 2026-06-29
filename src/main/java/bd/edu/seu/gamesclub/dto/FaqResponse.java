package bd.edu.seu.gamesclub.dto;

/** Read model for an FAQ. */
public record FaqResponse(
        Long id,
        String question,
        String answer,
        int displayOrder,
        boolean active
) {}
