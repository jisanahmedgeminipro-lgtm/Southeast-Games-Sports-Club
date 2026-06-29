package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Editable student profile fields (email and student id are immutable). */
public record StudentUpdateRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Size(max = 100) String department,
        @NotBlank @Size(max = 20) String batch,
        @NotBlank @Size(max = 20) String semester,
        @NotBlank @Size(max = 20) String phone,
        @NotBlank @Pattern(regexp = "MALE|FEMALE|OTHER", message = "invalid gender") String gender,
        Long profileMediaId
) {}
