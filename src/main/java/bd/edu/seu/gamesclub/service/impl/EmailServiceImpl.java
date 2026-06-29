package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.entity.MembershipPeriod;
import bd.edu.seu.gamesclub.entity.enums.OtpPurpose;
import bd.edu.seu.gamesclub.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * {@link EmailService} backed by Spring's {@link JavaMailSender}.
 *
 * <p>Every method is annotated {@code @Async} so dispatch runs on a background
 * thread (the application enables async via {@code @EnableAsync}). Failures are
 * logged rather than propagated, since callers (registration, broadcasts) must
 * not fail because a single email could not be delivered.
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String from;
    private final String clubName;
    private final int otpExpiryMinutes;

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${app.mail.from:}") String from,
                            @Value("${spring.mail.username:}") String mailUsername,
                            @Value("${app.club.name}") String clubName,
                            @Value("${app.otp.expiry-minutes:5}") int otpExpiryMinutes) {
        this.mailSender = mailSender;
        // Fall back to the authenticated SMTP account when no explicit From is set,
        // so Gmail does not reject/rewrite a mismatched sender address.
        this.from = (from == null || from.isBlank()) ? mailUsername : from;
        this.clubName = clubName;
        this.otpExpiryMinutes = otpExpiryMinutes;
    }

    @Override
    @Async
    public void sendOtp(String to, String otp, OtpPurpose purpose) {
        String title = purpose == OtpPurpose.REGISTRATION ? "Verify your email" : "Reset your password";
        String intro = purpose == OtpPurpose.REGISTRATION
                ? "Use the code below to complete your registration."
                : "Use the code below to reset your password.";
        String html = """
                <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto">
                  <h2 style="color:#0f766e">%s</h2>
                  <p>%s</p>
                  <p style="font-size:32px;font-weight:bold;letter-spacing:8px;color:#0f1c24">%s</p>
                  <p style="color:#5b6b78">This code expires in %d minutes. If you did not request this, please ignore this email.</p>
                  <hr><p style="color:#8595a3;font-size:12px">%s</p>
                </div>
                """.formatted(title, intro, otp, otpExpiryMinutes, clubName);
        send(to, title + " - " + clubName, html);
    }

    @Override
    @Async
    public void sendMembershipOpened(String to, String recipientName, MembershipPeriod period) {
        String html = """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto">
                  <h2 style="color:#0f766e">Membership is now open!</h2>
                  <p>Hi %s,</p>
                  <p>%s</p>
                  <p><strong>Opens:</strong> %s<br><strong>Closes:</strong> %s</p>
                  <p>Log in to your dashboard to submit your application.</p>
                  <hr><p style="color:#8595a3;font-size:12px">%s</p>
                </div>
                """.formatted(
                recipientName != null ? recipientName : "there",
                period.getAnnouncement() != null ? period.getAnnouncement() : "Applications for club membership are now being accepted.",
                period.getOpeningDate(), period.getClosingDate(), clubName);
        send(to, "Membership is open - " + clubName, html);
    }

    @Override
    @Async
    public void sendHtml(String to, String subject, String htmlBody) {
        send(to, subject, htmlBody);
    }

    @Override
    @Async
    public void sendMembershipDecision(String to, String recipientName, boolean approved, String remarks) {
        String heading = approved ? "Your membership is approved!" : "Membership application update";
        String message = approved
                ? "Congratulations! Your application to join the club has been approved. Welcome aboard."
                : "Thank you for applying. Unfortunately your application was not approved at this time.";
        String remarksBlock = (remarks != null && !remarks.isBlank())
                ? "<p style=\"color:#5b6b78\"><strong>Remarks:</strong> " + remarks + "</p>" : "";
        String html = """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto">
                  <h2 style="color:#0f766e">%s</h2>
                  <p>Hi %s,</p>
                  <p>%s</p>
                  %s
                  <hr><p style="color:#8595a3;font-size:12px">%s</p>
                </div>
                """.formatted(heading, recipientName != null ? recipientName : "there", message, remarksBlock, clubName);
        send(to, heading + " - " + clubName, html);
    }

    @Override
    @Async
    public void sendContactAutoReply(String to, String name) {
        String html = """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto">
                  <h2 style="color:#0f766e">We've received your message</h2>
                  <p>Hi %s,</p>
                  <p>Thank you for reaching out to the %s. Our team has received your message
                     and will get back to you as soon as possible.</p>
                  <p style="color:#5b6b78">This is an automated confirmation - please do not reply.</p>
                </div>
                """.formatted(name != null ? name : "there", clubName);
        send(to, "We've received your message - " + clubName, html);
    }

    /** Builds and sends a MIME (HTML) message; logs and swallows failures. */
    private void send(String to, String subject, String htmlBody) {
        if (from == null || from.isBlank()) {
            log.warn("Email NOT sent to {} - SMTP is not configured. Set MAIL_USERNAME, MAIL_PASSWORD "
                    + "(Gmail App Password) and optionally MAIL_FROM.", to);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} ({})", to, subject);
        } catch (Exception ex) {
            log.error("Failed to send email to {} ({}). Check SMTP credentials / Gmail App Password.", to, subject, ex);
        }
    }
}
