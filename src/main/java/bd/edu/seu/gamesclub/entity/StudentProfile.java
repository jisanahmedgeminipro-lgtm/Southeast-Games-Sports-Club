package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.Gender;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Student-specific profile data, in a 1:1 relationship with {@link User}.
 *
 * <p>This is the owning side of the relationship: it holds the {@code user_id}
 * foreign key (which is also unique). The association is {@code LAZY} and
 * unidirectional - {@link User} has no back-reference - to avoid circular
 * references. Deleting the owning {@link User} cascades to the profile at the
 * database level ({@code ON DELETE CASCADE}).
 */
@Entity
@Table(
        name = "student_profiles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_student_profiles_user", columnNames = "user_id"),
                @UniqueConstraint(name = "uq_student_profiles_student_id", columnNames = "student_id")
        },
        indexes = {
                @Index(name = "idx_student_profiles_department", columnList = "department"),
                @Index(name = "idx_student_profiles_batch", columnList = "batch")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class StudentProfile extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** The owning user account (1:1). */
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_student_profiles_user")
    )
    private User user;

    /** Full name of the student. */
    @NotBlank
    @Size(max = 120)
    @ToString.Include
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    /** University-issued student id (unique). */
    @NotBlank
    @Size(max = 30)
    @ToString.Include
    @Column(name = "student_id", nullable = false, length = 30)
    private String studentId;

    /** Academic department. */
    @NotBlank
    @Size(max = 100)
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    /** Admission batch (e.g. {@code "63"}). */
    @NotBlank
    @Size(max = 20)
    @Column(name = "batch", nullable = false, length = 20)
    private String batch;

    /** Current semester. */
    @NotBlank
    @Size(max = 20)
    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    /** Contact phone number. */
    @NotBlank
    @Size(max = 20)
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    /** Gender. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    /** Optional profile picture (reference into the media library). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "profile_media_id",
            foreignKey = @ForeignKey(name = "fk_student_profiles_media")
    )
    private MediaAsset profilePicture;

    /** Admin soft-disable flag for the profile. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
