package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Payload to start the forgot-password flow. */
public record ForgotPasswordRequest(
        @NotBlank @Email String email
) {}
