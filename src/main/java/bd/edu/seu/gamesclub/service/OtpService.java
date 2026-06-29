package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.entity.enums.OtpPurpose;

/**
 * Issues and verifies one-time passwords for registration and password reset.
 *
 * <p>Business rules enforced here: OTPs are stored hashed (never plain), expire
 * after 5 minutes, can be used only once, and may be resent only after a
 * 60-second cooldown.
 */
public interface OtpService {

    /**
     * Generate a new OTP for the email/purpose and dispatch it by email.
     * Enforces the resend cooldown.
     */
    void generateAndSend(String email, OtpPurpose purpose);

    /**
     * Verify a submitted code. Marks the matching token used on success.
     *
     * @throws bd.edu.seu.gamesclub.exception.InvalidOtpException if missing/incorrect/already used
     * @throws bd.edu.seu.gamesclub.exception.OtpExpiredException if past its validity window
     */
    void verify(String email, String otp, OtpPurpose purpose);

    /** Remove expired tokens (invoked by the scheduled cleanup job). */
    int purgeExpired();
}
