package bd.edu.seu.gamesclub.dto;

import java.time.LocalDate;

/** Read model for a notice. */
public record NoticeResponse(
        Long id,
        String title,
        String content,
        String noticeType,
        LocalDate publishDate,
        LocalDate expiryDate,
        boolean pinned,
        boolean published
) {}
