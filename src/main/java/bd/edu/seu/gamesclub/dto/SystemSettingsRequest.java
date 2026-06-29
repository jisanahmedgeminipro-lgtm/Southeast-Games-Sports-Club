package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Update payload for the singleton system settings. */
public record SystemSettingsRequest(
        @NotBlank @Size(max = 160) String siteTitle,
        Long faviconMediaId,
        @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6})$", message = "must be a valid hex color") String themeColor,
        @Size(max = 255) String footerCopyright,
        @Size(max = 120) String smtpSenderName,
        Boolean maintenanceMode
) {}
