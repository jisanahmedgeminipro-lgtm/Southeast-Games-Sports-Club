package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.entity.OtpToken;
import bd.edu.seu.gamesclub.entity.enums.OtpPurpose;
import bd.edu.seu.gamesclub.exception.InvalidOtpException;
import bd.edu.seu.gamesclub.exception.OtpExpiredException;
import bd.edu.seu.gamesclub.exception.ValidationException;
import bd.edu.seu.gamesclub.repository.OtpTokenRepository;
import bd.edu.seu.gamesclub.service.EmailService;
import bd.edu.seu.gamesclub.service.OtpService;
import bd.edu.seu.gamesclub.util.OtpGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Default {@link OtpService}. OTP codes are hashed with the application
 * {@link PasswordEncoder} (BCrypt) before storage.
 */
@Service
public class OtpServiceImpl implements OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final int otpLength;
    private final int expiryMinutes;
    private final int resendCooldownSeconds;

    public OtpServiceImpl(OtpTokenRepository otpTokenRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService,
                          @Value("${app.otp.length:6}") int otpLength,
                          @Value("${app.otp.expiry-minutes:5}") int expiryMinutes,
                          @Value("${app.otp.resend-cooldown-seconds:60}") int resendCooldownSeconds) {
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpLength = otpLength;
        this.expiryMinutes = expiryMinutes;
        this.resendCooldownSeconds = resendCooldownSeconds;
    }

    @Override
    @Transactional
    public void generateAndSend(String email, OtpPurpose purpose) {
        LocalDateTime now = LocalDateTime.now();

        // Enforce the 60-second resend cooldown against the most recent token.
        otpTokenRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .ifPresent(last -> {
                    if (last.getLastSentAt() != null
                            && last.getLastSentAt().plusSeconds(resendCooldownSeconds).isAfter(now)) {
                        throw new ValidationException("Please wait a moment before requesting another code.");
                    }
                });

        String code = OtpGenerator.generate(otpLength);
        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtpHash(passwordEncoder.encode(code));
        token.setPurpose(purpose);
        token.setExpiresAt(now.plusMinutes(expiryMinutes));
        token.setLastSentAt(now);
        token.setUsed(false);
        otpTokenRepository.save(token);

        emailService.sendOtp(email, code, purpose);
    }

    @Override
    @Transactional
    public void verify(String email, String otp, OtpPurpose purpose) {
        OtpToken token = otpTokenRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new InvalidOtpException("No verification code found. Please request a new one."));

        if (token.isUsed()) {
            throw new InvalidOtpException("This code has already been used. Please request a new one.");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("This code has expired. Please request a new one.");
        }
        if (!passwordEncoder.matches(otp, token.getOtpHash())) {
            token.setAttemptCount(token.getAttemptCount() + 1);
            otpTokenRepository.save(token);
            throw new InvalidOtpException("The code you entered is incorrect.");
        }

        token.setUsed(true);
        token.setUsedAt(LocalDateTime.now());
        otpTokenRepository.save(token);
    }

    @Override
    @Transactional
    public int purgeExpired() {
        return otpTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
