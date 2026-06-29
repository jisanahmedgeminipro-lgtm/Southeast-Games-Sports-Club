package bd.edu.seu.gamesclub.dto;

import java.time.LocalDate;

/** Read model for a news article. */
public record NewsResponse(
        Long id,
        String title,
        String slug,
        String content,
        String imageUrl,
        LocalDate publishDate,
        String status,
        Long imageMediaId
) {}
