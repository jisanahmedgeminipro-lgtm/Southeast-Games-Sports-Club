package bd.edu.seu.gamesclub.dto;

import bd.edu.seu.gamesclub.validation.SeuEmail;
import bd.edu.seu.gamesclub.validation.StudentId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Student self-registration payload. Domain rules (allowed email domain, unique
 * email/student id, password match) are enforced in the service layer.
 */
public record RegisterRequest(
        @NotBlank @Email @SeuEmail @Size(max = 150) String email,
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @StudentId @Size(max = 30) String studentId,
        @NotBlank @Size(max = 100) String department,
        @NotBlank @Size(max = 20) String batch,
        @NotBlank @Size(max = 20) String semester,
        @NotBlank @Size(max = 20) String phone,
        @NotBlank @Pattern(regexp = "MALE|FEMALE|OTHER", message = "invalid gender") String gender,
        @NotBlank @Size(min = 6, max = 64) String password,
        @NotBlank String confirmPassword
) {}
