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
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A single slide in the dynamic landing-page hero section. Admins manage these
 * (text, call-to-action, background image, ordering, active flag) so the hero is
 * fully content-managed.
 */
@Entity
@Table(
        name = "hero_sliders",
        indexes = @Index(name = "idx_hero_active_order", columnList = "is_active, display_order")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class HeroSlider extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Headline. */
    @Size(max = 180)
    @ToString.Include
    @Column(name = "title", length = 180)
    private String title;

    /** Sub-headline. */
    @Size(max = 220)
    @Column(name = "subtitle", length = 220)
    private String subtitle;

    /** Supporting description. */
    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Call-to-action button label. */
    @Size(max = 60)
    @Column(name = "button_text", length = 60)
    private String buttonText;

    /** Call-to-action button target URL. */
    @Size(max = 255)
    @Column(name = "button_url", length = 255)
    private String buttonUrl;

    /** Background image (media library reference). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_media_id", foreignKey = @ForeignKey(name = "fk_hero_background"))
    private MediaAsset background;

    /** Manual ordering of slides. */
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    /** Whether the slide is shown. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
