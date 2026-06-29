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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * A single image within a {@link GalleryCategory}.
 *
 * <p>The owning side of the category relationship (holds {@code category_id}).
 * The actual file lives in the centralized {@link MediaAsset} library; the
 * reference uses {@code ON DELETE RESTRICT} (the default) so an in-use album
 * image cannot be orphaned. Deleting the parent category cascades here.
 */
@Entity
@Table(
        name = "gallery_images",
        indexes = @Index(name = "idx_gallery_images_category", columnList = "category_id, display_order")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class GalleryImage extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Owning album/category. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_gallery_images_category")
    )
    private GalleryCategory category;

    /** The underlying media file (required). */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "media_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_gallery_images_media")
    )
    private MediaAsset media;

    /** Optional caption. */
    @Size(max = 255)
    @Column(name = "caption", length = 255)
    private String caption;

    /** Manual ordering within the album. */
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
}
