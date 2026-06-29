package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Create/update payload for a notice. */
public record NoticeRequest(
        @NotBlank @Size(max = 180) String title,
        @NotBlank String content,
        @NotBlank @Pattern(regexp = "PRACTICE|TOURNAMENT|HOLIDAY|GENERAL", message = "invalid notice type") String noticeType,
        LocalDate publishDate,
        LocalDate expiryDate,
        Boolean pinned,
        Boolean published
) {}
