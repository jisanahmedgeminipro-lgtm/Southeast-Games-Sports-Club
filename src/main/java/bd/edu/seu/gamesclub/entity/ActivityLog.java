package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Audit trail of important admin actions (membership opened, content created,
 * student disabled, ...).
 *
 * <p>The acting user is the inherited {@code created_by} field, so no separate
 * actor column is needed. {@code entityType} + {@code entityId} loosely reference
 * the affected record without a hard foreign key (the target may be any table or
 * may later be deleted).
 */
@Entity
@Table(
        name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_logs_created_by", columnList = "created_by"),
                @Index(name = "idx_activity_logs_created_at", columnList = "created_at"),
                @Index(name = "idx_activity_logs_entity", columnList = "entity_type, entity_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ActivityLog extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Machine-readable action code, e.g. {@code MEMBERSHIP_OPENED}. */
    @NotBlank
    @Size(max = 100)
    @ToString.Include
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    /** Type of the affected entity, e.g. {@code Event} (optional). */
    @Size(max = 60)
    @Column(name = "entity_type", length = 60)
    private String entityType;

    /** Id of the affected entity (optional). */
    @Column(name = "entity_id")
    private Long entityId;

    /** Human-readable description of what happened. */
    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    /** Originating IP address. */
    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /** Originating user agent. */
    @Size(max = 255)
    @Column(name = "user_agent", length = 255)
    private String userAgent;
}
