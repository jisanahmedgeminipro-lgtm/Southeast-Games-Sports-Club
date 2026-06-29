package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A club accomplishment (trophy, title, ranking) earned in a given year,
 * optionally tied to a {@link Sport}.
 */
@Entity
@Table(
        name = "achievements",
        indexes = @Index(name = "idx_achievements_year", columnList = "achievement_year")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Achievement extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Achievement title. */
    @NotBlank
    @Size(max = 180)
    @ToString.Include
    @Column(name = "title", nullable = false, length = 180)
    private String title;

    /** Year the achievement was earned. */
    @NotNull
    @Min(1990)
    @Max(2100)
    @ToString.Include
    @Column(name = "achievement_year", nullable = false)
    private Short achievementYear;

    /** Optional description. */
    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Optional image. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_media_id", foreignKey = @ForeignKey(name = "fk_achievements_image"))
    private MediaAsset image;

    /** Optional related sport (nullable). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", foreignKey = @ForeignKey(name = "fk_achievements_sport"))
    private Sport sport;

    /** Manual ordering for display. */
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
}
