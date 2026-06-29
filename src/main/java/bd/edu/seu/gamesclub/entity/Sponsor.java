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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A club sponsor displayed on the landing page.
 */
@Entity
@Table(
        name = "sponsors",
        indexes = @Index(name = "idx_sponsors_active_order", columnList = "is_active, display_order")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Sponsor extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Sponsor name. */
    @NotBlank
    @Size(max = 120)
    @ToString.Include
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    /** Optional sponsor logo (media library reference). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_media_id", foreignKey = @ForeignKey(name = "fk_sponsors_logo"))
    private MediaAsset logo;

    /** Optional sponsor website URL. */
    @Size(max = 255)
    @Column(name = "website", length = 255)
    private String website;

    /** Manual ordering for display. */
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    /** Whether the sponsor is shown. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
