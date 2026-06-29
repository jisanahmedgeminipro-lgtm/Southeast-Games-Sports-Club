package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Payload to reset a password using an OTP. */
public record ResetPasswordRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits") String otp,
        @NotBlank @Size(min = 6, max = 64) String password,
        @NotBlank String confirmPassword
) {}
