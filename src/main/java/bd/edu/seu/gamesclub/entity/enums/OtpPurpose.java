package bd.edu.seu.gamesclub.entity.enums;

/** Reason an OTP was issued ({@code otp_tokens.purpose}). Stored as {@code STRING}. */
public enum OtpPurpose {

    /** OTP for verifying a new student registration. */
    REGISTRATION,

    /** OTP for the forgot-password reset flow. */
    FORGOT_PASSWORD
}
