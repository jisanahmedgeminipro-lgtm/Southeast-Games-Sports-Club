package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.ClubSettingsRequest;
import bd.edu.seu.gamesclub.dto.ClubSettingsResponse;

/** Reads and updates the singleton club identity/contact settings. */
public interface ClubSettingsService {

    /** Get the settings, creating sensible defaults on first access. */
    ClubSettingsResponse get();

    /** Update the settings. */
    ClubSettingsResponse update(ClubSettingsRequest request);
}
