package bd.edu.seu.gamesclub.dto;

/** Read model for an achievement. */
public record AchievementResponse(
        Long id,
        String title,
        Short achievementYear,
        String description,
        String imageUrl,
        Long sportId,
        String sportName,
        int displayOrder,
        Long imageMediaId
) {}
