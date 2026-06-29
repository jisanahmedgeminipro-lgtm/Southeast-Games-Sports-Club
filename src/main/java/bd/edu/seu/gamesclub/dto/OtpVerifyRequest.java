package bd.edu.seu.gamesclub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Payload to verify a registration OTP. */
public record OtpVerifyRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits") String otp
) {}
