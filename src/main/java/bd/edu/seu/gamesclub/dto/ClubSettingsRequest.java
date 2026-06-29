package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Update payload for the singleton club settings. */
public record ClubSettingsRequest(
        @NotBlank @Size(max = 160) String universityName,
        @NotBlank @Size(max = 160) String clubName,
        Long logoMediaId,
        @Min(1900) @Max(2100) Short establishedYear,
        @Size(max = 255) String motto,
        String aboutClub,
        @Size(max = 255) String address,
        @Size(max = 40) String phone,
        @Email @Size(max = 150) String email,
        @Size(max = 500) String googleMapLink
) {}
