package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.ForgotPasswordRequest;
import bd.edu.seu.gamesclub.dto.OtpVerifyRequest;
import bd.edu.seu.gamesclub.dto.RegisterRequest;
import bd.edu.seu.gamesclub.dto.ResetPasswordRequest;

/**
 * Authentication / account lifecycle use cases: registration with email
 * verification and the forgot-password reset flow.
 *
 * <p>Enforced rules: only {@code @seu.edu.bd} emails may register, email and
 * student id must be unique, passwords are BCrypt-encrypted, and the account is
 * only marked verified once the emailed OTP is confirmed.
 */
public interface AuthService {

    /**
     * Begin student registration: validate input, persist an unverified account,
     * and email a verification OTP.
     */
    void register(RegisterRequest request);

    /** Confirm a registration OTP and mark the account email-verified. */
    void verifyRegistration(OtpVerifyRequest request);

    /** Resend the registration OTP (subject to the cooldown). */
    void resendRegistrationOtp(String email);

    /** Start the forgot-password flow by emailing a reset OTP (silent if unknown email). */
    void initiatePasswordReset(ForgotPasswordRequest request);

    /** Complete a password reset using a valid OTP. */
    void resetPassword(ResetPasswordRequest request);
}
