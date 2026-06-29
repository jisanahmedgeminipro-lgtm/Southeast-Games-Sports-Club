package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A sport offered by the club (Football, Cricket, ...). The seven defaults are
 * inserted as seed data; admins can add more at runtime.
 *
 * <p>References to images use the centralized {@link MediaAsset} library via
 * lazy {@code @ManyToOne} associations.
 */
@Entity
@Table(
        name = "sports",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_sports_name", columnNames = "name"),
                @UniqueConstraint(name = "uq_sports_slug", columnNames = "slug")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Sport extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Unique display name. */
    @NotBlank
    @Size(max = 80)
    @ToString.Include
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    /** Unique URL slug. */
    @NotBlank
    @Size(max = 100)
    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    /** Optional description. */
    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Optional small icon image. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_media_id", foreignKey = @ForeignKey(name = "fk_sports_icon"))
    private MediaAsset icon;

    /** Optional larger cover image. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_media_id", foreignKey = @ForeignKey(name = "fk_sports_image"))
    private MediaAsset image;

    /** Manual ordering for display. */
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    /** Whether the sport is shown publicly. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
