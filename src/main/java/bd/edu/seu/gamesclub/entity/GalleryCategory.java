package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A gallery album / category that groups multiple {@link GalleryImage}s.
 *
 * <p>This is one of the few intentionally bidirectional relationships: a
 * category <em>owns</em> its images, so the collection uses
 * {@code cascade = ALL} and {@code orphanRemoval = true} (removing an image from
 * the list deletes it). The collection is {@code LAZY} and excluded from
 * {@code toString}/{@code equals} (only the id is included) to prevent recursion
 * and large fetches.
 */
@Entity
@Table(
        name = "gallery_categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_gallery_categories_name", columnNames = "name"),
                @UniqueConstraint(name = "uq_gallery_categories_slug", columnNames = "slug")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class GalleryCategory extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Unique category name. */
    @NotBlank
    @Size(max = 100)
    @ToString.Include
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** Unique URL slug. */
    @NotBlank
    @Size(max = 120)
    @Column(name = "slug", nullable = false, length = 120)
    private String slug;

    /** Optional short description. */
    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    /** Images belonging to this category (owned aggregate). */
    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = jakarta.persistence.FetchType.LAZY
    )
    @OrderBy("displayOrder ASC")
    private List<GalleryImage> images = new ArrayList<>();
}
