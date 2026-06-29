package bd.edu.seu.gamesclub.dto;

/** Read model for the singleton club settings. */
public record ClubSettingsResponse(
        Long id,
        String universityName,
        String clubName,
        String logoUrl,
        Short establishedYear,
        String motto,
        String aboutClub,
        String address,
        String phone,
        String email,
        String googleMapLink,
        Long logoMediaId
) {}
