package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.OtpToken;
import bd.edu.seu.gamesclub.entity.enums.OtpPurpose;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link OtpToken}s used in registration and password reset.
 */
@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    /**
     * Find the latest <em>valid</em> OTP for an email + purpose: not yet used and
     * not yet expired. Used during verification.
     */
    Optional<OtpToken> findTopByEmailAndPurposeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String email, OtpPurpose purpose, LocalDateTime now);

    /**
     * Find the most recent OTP for an email + purpose regardless of state, used to
     * enforce the 60-second resend cooldown via {@code lastSentAt}.
     */
    Optional<OtpToken> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);

    /**
     * Bulk-delete expired tokens (scheduled cleanup). A bulk {@code @Query} is used
     * here deliberately so cleanup is a single statement rather than per-row loads.
     */
    @Modifying
    @Query("delete from OtpToken o where o.expiresAt < :cutoff")
    int deleteExpiredTokens(@Param("cutoff") LocalDateTime cutoff);
}
