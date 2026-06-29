package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Create/update payload for a membership drive. */
public record MembershipPeriodRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 5000) String announcement,
        @NotNull LocalDate openingDate,
        @NotNull LocalDate closingDate
) {}
