package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
 * A social media link, the single source of truth for the site's social URLs
 * (header/footer icons).
 *
 * <p>{@code platform} is stored as a free-form {@link String} (with a unique
 * constraint) rather than an enum, so admins can add new platforms without code
 * changes - the core intent of keeping social links data-driven.
 */
@Entity
@Table(
        name = "social_links",
        uniqueConstraints = @UniqueConstraint(name = "uq_social_links_platform", columnNames = "platform"),
        indexes = @Index(name = "idx_social_active_order", columnList = "is_active, display_order")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SocialLink extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Platform key, e.g. {@code FACEBOOK}, {@code INSTAGRAM} (unique). */
    @NotBlank
    @Size(max = 50)
    @ToString.Include
    @Column(name = "platform", nullable = false, length = 50)
    private String platform;

    /** The social profile/page URL. */
    @NotBlank
    @Size(max = 255)
    @Column(name = "url", nullable = false, length = 255)
    private String url;

    /** Optional icon CSS class, e.g. {@code bi-facebook}. */
    @Size(max = 60)
    @Column(name = "icon_class", length = 60)
    private String iconClass;

    /** Manual ordering for display. */
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    /** Whether the link is shown. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
