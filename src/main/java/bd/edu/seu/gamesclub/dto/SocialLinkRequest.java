package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Create/update payload for a social link. Platform must be unique. */
public record SocialLinkRequest(
        @NotBlank @Size(max = 50) String platform,
        @NotBlank @Size(max = 255) String url,
        @Size(max = 60) String iconClass,
        Integer displayOrder,
        Boolean active
) {}
