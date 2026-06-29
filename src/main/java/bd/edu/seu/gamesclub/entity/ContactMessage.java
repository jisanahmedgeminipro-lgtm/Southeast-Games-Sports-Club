package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A message submitted through the public contact form.
 *
 * <p>Submitted by anonymous visitors, so {@code created_by} (inherited) is
 * typically {@code null}; an admin handling the message becomes
 * {@code updated_by}.
 */
@Entity
@Table(
        name = "contact_messages",
        indexes = @Index(name = "idx_contact_messages_is_read", columnList = "is_read")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ContactMessage extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Sender name. */
    @NotBlank
    @Size(max = 120)
    @ToString.Include
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    /** Sender email. */
    @NotBlank
    @Email
    @Size(max = 150)
    @ToString.Include
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    /** Optional subject. */
    @Size(max = 180)
    @Column(name = "subject", length = 180)
    private String subject;

    /** Message body. */
    @NotBlank
    @Lob
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /** Whether an admin has read the message. */
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    /** Originating IP address (IPv4/IPv6) for abuse tracking. */
    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
