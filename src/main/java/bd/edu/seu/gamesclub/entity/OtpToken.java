package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.OtpPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A one-time password issued for registration or password reset.
 *
 * <p>The OTP itself is never stored in clear text - only a hash
 * ({@code otpHash}) is persisted. Tokens are keyed by {@code email} (not a
 * {@link User} FK) because a registration OTP is issued <em>before</em> the user
 * account exists. Expiry, single-use ({@code used}), attempt counting and the
 * 60-second resend gate ({@code lastSentAt}) are all enforced by the auth service.
 */
@Entity
@Table(
        name = "otp_tokens",
        indexes = {
                @Index(name = "idx_otp_email_purpose", columnList = "email, purpose"),
                @Index(name = "idx_otp_expires_at", columnList = "expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class OtpToken extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Target email address (the account may not exist yet). */
    @NotBlank
    @Email
    @Size(max = 150)
    @ToString.Include
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    /** Hash of the 6-digit OTP code. */
    @NotBlank
    @Size(max = 100)
    @Column(name = "otp_hash", nullable = false, length = 100)
    private String otpHash;

    /** Why the OTP was issued. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "purpose", nullable = false, length = 20)
    private OtpPurpose purpose;

    /** Expiry instant (issued + 5 minutes). */
    @NotNull
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Whether the OTP has already been consumed (single-use). */
    @Column(name = "is_used", nullable = false)
    private boolean used = false;

    /** When the OTP was consumed. */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /** Number of verification attempts made against this token. */
    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    /** Last time a code was sent (drives the 60-second resend cooldown). */
    @NotNull
    @Column(name = "last_sent_at", nullable = false)
    private LocalDateTime lastSentAt;
}
