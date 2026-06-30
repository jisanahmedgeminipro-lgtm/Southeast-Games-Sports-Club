package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Common auditing base class extended by <strong>every</strong> entity in the system.
 *
 * <p>Provides the standard audit columns required on all business tables:
 * <ul>
 *     <li>{@code created_by} / {@code updated_by} - the id of the {@link User}
 *         that created / last modified the row. These are populated automatically
 *         by Spring Data JPA auditing via an {@code AuditorAware<Long>} bean
 *         (configured in the persistence/config layer).</li>
 *     <li>{@code created_at} / {@code updated_at} - automatically managed
 *         timestamps.</li>
 * </ul>
 *
 * <p>The {@code created_by}/{@code updated_by} values are stored as plain
 * {@link Long} user ids rather than {@code @ManyToOne} associations. This is a
 * deliberate, production-oriented choice: it avoids loading a {@code User}
 * (and the resulting N+1 queries) on every audited entity and prevents circular
 * references, while the foreign keys to {@code users(id)} are still enforced at
 * the database level (see the canonical schema in {@code docs/DATABASE_DESIGN.md}).
 *
 * @author SEU Games &amp; Sports Club Dev Team
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable implements Serializable {

    /** Id of the user who created the record (FK to {@code users.id}). */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    /** Id of the user who last modified the record (FK to {@code users.id}). */
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    /** Timestamp set once when the record is first persisted. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp refreshed on every update. */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Safety net so the NOT NULL audit timestamps are <em>always</em> populated,
     * even if Spring Data JPA auditing is unavailable for some reason. This runs
     * in addition to {@link AuditingEntityListener} and prevents
     * "Could not commit JPA transaction" failures caused by null timestamps.
     */
    @PrePersist
    void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void onPreUpdate() {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
}
