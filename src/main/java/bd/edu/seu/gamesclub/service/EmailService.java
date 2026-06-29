package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.entity.MembershipPeriod;
import bd.edu.seu.gamesclub.entity.enums.OtpPurpose;

/**
 * Outbound e-mail operations. All implementations send asynchronously so HTTP
 * requests are never blocked by SMTP latency.
 */
public interface EmailService {

    /**
     * Send a one-time-password email for registration or password reset.
     *
     * @param to      recipient address
     * @param otp     the plain OTP code (only emailed, never stored in clear text)
     * @param purpose the reason the OTP was issued
     */
    void sendOtp(String to, String otp, OtpPurpose purpose);

    /**
     * Notify a registered student that a membership drive has opened.
     *
     * @param to          recipient address
     * @param recipientName display name for the greeting
     * @param period      the membership period that just opened
     */
    void sendMembershipOpened(String to, String recipientName, MembershipPeriod period);

    /**
     * Notify a student of the decision on their membership application.
     *
     * @param to            recipient address
     * @param recipientName display name for the greeting
     * @param approved      whether the application was approved
     * @param remarks       optional reviewer remarks
     */
    void sendMembershipDecision(String to, String recipientName, boolean approved, String remarks);

    /**
     * Auto-reply confirming receipt of a contact-form submission.
     *
     * @param to   the visitor's email
     * @param name the visitor's name
     */
    void sendContactAutoReply(String to, String name);

    /**
     * Send an arbitrary HTML email (used by admin broadcasts).
     *
     * @param to       recipient address
     * @param subject  subject line
     * @param htmlBody HTML body
     */
    void sendHtml(String to, String subject, String htmlBody);
}
