package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * A student's application to join the club during a specific
 * {@link MembershipPeriod}.
 *
 * <p>A unique constraint on {@code (membership_period_id, student_user_id)}
 * guarantees one application per student per drive. The applicant
 * ({@code student}) and the reviewing admin ({@code reviewedBy}) act as the
 * natural domain actors in addition to the inherited audit fields. Both
 * associations are {@code LAZY}; deleting the period or the student cascades to
 * the application at the database level.
 */
@Entity
@Table(
        name = "membership_applications",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_membership_application",
                columnNames = {"membership_period_id", "student_user_id"}
        ),
        indexes = {
                @Index(name = "idx_membership_app_student", columnList = "student_user_id"),
                @Index(name = "idx_membership_app_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MembershipApplication extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** The membership drive this application belongs to. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "membership_period_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_membership_app_period")
    )
    private MembershipPeriod period;

    /** The applying student (a {@link User} with role STUDENT). */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "student_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_membership_app_student")
    )
    private User student;

    /** Current decision status. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "status", nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    /** When the student submitted the application. */
    @NotNull
    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    /** Admin who reviewed the application (nullable until reviewed). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reviewed_by",
            foreignKey = @ForeignKey(name = "fk_membership_app_reviewer")
    )
    private User reviewedBy;

    /** When the application was reviewed. */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /** Optional reviewer remarks. */
    @Size(max = 500)
    @Column(name = "remarks", length = 500)
    private String remarks;
}
