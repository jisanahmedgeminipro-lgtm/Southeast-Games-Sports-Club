package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.SystemSettingsRequest;
import bd.edu.seu.gamesclub.dto.SystemSettingsResponse;

/** Reads and updates the singleton site/application settings. */
public interface SystemSettingsService {

    /** Get the settings, creating sensible defaults on first access. */
    SystemSettingsResponse get();

    /** Update the settings. */
    SystemSettingsResponse update(SystemSettingsRequest request);

    /** Whether the public site is currently in maintenance mode. */
    boolean isMaintenanceMode();
}
