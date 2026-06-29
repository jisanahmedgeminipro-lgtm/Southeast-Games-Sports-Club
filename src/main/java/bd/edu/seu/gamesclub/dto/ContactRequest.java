package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Public contact-form submission. */
public record ContactRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 180) String subject,
        @NotBlank @Size(max = 5000) String message
) {}
