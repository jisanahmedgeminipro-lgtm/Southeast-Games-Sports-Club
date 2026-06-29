package bd.edu.seu.gamesclub.dto;

/** Read model for the singleton system settings. */
public record SystemSettingsResponse(
        Long id,
        String siteTitle,
        String faviconUrl,
        String themeColor,
        String footerCopyright,
        String smtpSenderName,
        boolean maintenanceMode,
        Long faviconMediaId
) {}
