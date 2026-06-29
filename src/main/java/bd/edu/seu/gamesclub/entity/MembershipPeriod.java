package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.MembershipStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A single membership drive (one open/close cycle).
 *
 * <p>Membership is never open all year. The admin creates a period, sets the
 * opening/closing dates and announcement, then opens it. When opened, a
 * notification email is sent to every registered student (tracked by
 * {@code notificationSent}). The landing page derives the current "open/closed"
 * state from the active period, so no duplicate flag is stored.
 *
 * <p>The relationship to {@link MembershipApplication} is deliberately
 * unidirectional (no inverse collection here) to avoid loading potentially
 * thousands of applications when a period is fetched.
 */
@Entity
@Table(
        name = "membership_periods",
        indexes = {
                @Index(name = "idx_membership_periods_status", columnList = "status"),
                @Index(name = "idx_membership_periods_dates", columnList = "opening_date, closing_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MembershipPeriod extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Human-friendly drive title, e.g. {@code "Membership Drive Fall 2026"}. */
    @NotBlank
    @Size(max = 120)
    @ToString.Include
    @Column(name = "title", nullable = false, length = 120)
    private String title;

    /** Announcement text shown on the landing page for this drive. */
    @Lob
    @Column(name = "announcement", columnDefinition = "TEXT")
    private String announcement;

    /** Date the drive opens. */
    @NotNull
    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    /** Date the drive closes (must be on/after {@link #openingDate}). */
    @NotNull
    @Column(name = "closing_date", nullable = false)
    private LocalDate closingDate;

    /** Lifecycle status. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "status", nullable = false, length = 20)
    private MembershipStatus status = MembershipStatus.DRAFT;

    /** When the drive was actually opened. */
    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    /** When the drive was actually closed. */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /** Whether the "membership opened" broadcast has been sent (idempotency guard). */
    @Column(name = "is_notification_sent", nullable = false)
    private boolean notificationSent = false;
}
