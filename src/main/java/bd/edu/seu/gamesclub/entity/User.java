package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
 * Authentication account for both administrators and students.
 *
 * <p>This table stores only credential / security data shared by every user.
 * Student-specific attributes live in {@link StudentProfile} (1:1). Admin
 * accounts are created manually in the database; students self-register.
 *
 * <p>The {@code @seu.edu.bd} domain restriction for student registration is
 * enforced in the service/validation layer (not as a column constraint) so that
 * manually-seeded admin accounts remain flexible.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uq_users_email", columnNames = "email"),
        indexes = @Index(name = "idx_users_role", columnList = "role")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Unique login email. */
    @NotBlank
    @Email
    @Size(max = 150)
    @ToString.Include
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    /** BCrypt password hash (never the raw password). */
    @NotBlank
    @Size(max = 100)
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    /** Security role used by Spring Security. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    /** Whether the email has been verified via OTP. */
    @Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified = false;

    /** Whether the account is allowed to authenticate. */
    @Column(name = "is_enabled", nullable = false)
    private boolean enabled = true;

    /** Timestamp of the most recent successful login. */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
