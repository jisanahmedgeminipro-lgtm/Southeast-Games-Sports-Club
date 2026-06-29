package bd.edu.seu.gamesclub.entity;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * A recurring weekly practice slot for a {@link Sport}.
 *
 * <p>{@code dayOfWeek} uses {@link java.time.DayOfWeek} (an enum) persisted as
 * {@code STRING}, matching the {@code MONDAY..SUNDAY} CHECK constraint. Deleting
 * the parent sport cascades to its schedules.
 */
@Entity
@Table(
        name = "training_schedules",
        indexes = @Index(name = "idx_training_sport_day", columnList = "sport_id, day_of_week")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class TrainingSchedule extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** The sport being practiced. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "sport_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_training_sport")
    )
    private Sport sport;

    /** Day of the week. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    /** Session start time. */
    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /** Session end time (must be after {@link #startTime}). */
    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /** Practice venue. */
    @Size(max = 160)
    @Column(name = "venue", length = 160)
    private String venue;

    /** Coach name. */
    @Size(max = 120)
    @Column(name = "coach_name", length = 120)
    private String coachName;

    /** Whether the schedule is active. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
