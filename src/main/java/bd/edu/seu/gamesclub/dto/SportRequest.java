package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Create/update payload for a sport. The slug is derived from the name. */
public record SportRequest(
        @NotBlank @Size(max = 80) String name,
        @Size(max = 5000) String description,
        Long iconMediaId,
        Long imageMediaId,
        Integer displayOrder,
        Boolean active
) {}
