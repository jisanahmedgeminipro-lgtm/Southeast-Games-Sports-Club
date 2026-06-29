package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Create/update payload for a committee member. */
public record CommitteeMemberRequest(
        @NotBlank @Pattern(regexp = "EXECUTIVE|SUB_EXECUTIVE", message = "invalid committee type") String committeeType,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 100) String department,
        @NotBlank @Size(max = 20) String batch,
        @NotBlank @Size(max = 80) String position,
        Long photoMediaId,
        @Size(max = 255) String facebookUrl,
        @Size(max = 255) String linkedinUrl,
        @Size(max = 20) String sessionYear,
        Integer displayOrder,
        Boolean active
) {}
