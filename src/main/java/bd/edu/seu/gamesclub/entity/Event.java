package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.EventStatus;
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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A club event (tournament, friendly match, ceremony, ...).
 *
 * <p>May optionally be associated with a {@link Sport} (lazy, nullable) and a
 * banner image from the {@link MediaAsset} library.
 */
@Entity
@Table(
        name = "events",
        uniqueConstraints = @UniqueConstraint(name = "uq_events_slug", columnNames = "slug"),
        indexes = {
                @Index(name = "idx_events_event_date", columnList = "event_date"),
                @Index(name = "idx_events_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Event extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Event title. */
    @NotBlank
    @Size(max = 160)
    @ToString.Include
    @Column(name = "title", nullable = false, length = 160)
    private String title;

    /** Unique URL slug. */
    @NotBlank
    @Size(max = 180)
    @Column(name = "slug", nullable = false, length = 180)
    private String slug;

    /** Long description. */
    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Optional banner image. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_media_id", foreignKey = @ForeignKey(name = "fk_events_banner"))
    private MediaAsset banner;

    /** Venue / location. */
    @Size(max = 160)
    @Column(name = "venue", length = 160)
    private String venue;

    /** Event date. */
    @NotNull
    @ToString.Include
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    /** Event start time (optional). */
    @Column(name = "event_time")
    private LocalTime eventTime;

    /** Optional registration deadline. */
    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    /** Lifecycle status. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "status", nullable = false, length = 20)
    private EventStatus status = EventStatus.UPCOMING;

    /** Optional related sport (nullable). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", foreignKey = @ForeignKey(name = "fk_events_sport"))
    private Sport sport;
}
