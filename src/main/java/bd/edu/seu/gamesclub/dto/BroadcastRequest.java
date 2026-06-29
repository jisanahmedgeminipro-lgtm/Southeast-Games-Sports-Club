package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Admin email broadcast request. {@code recipientUserIds} is required only when
 * {@code targetType} is {@code SELECTED}.
 */
public record BroadcastRequest(
        @NotBlank @Size(max = 200) String subject,
        @NotBlank String body,
        @NotNull @Pattern(regexp = "ALL_STUDENTS|MEMBERS_ONLY|SELECTED", message = "invalid target") String targetType,
        List<Long> recipientUserIds
) {}
