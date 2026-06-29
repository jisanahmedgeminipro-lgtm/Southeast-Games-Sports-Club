package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.MediaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Central media library entry.
 *
 * <p>Every uploaded file (committee photos, event banners, gallery images, the
 * club logo, favicon, hero backgrounds, sponsor logos, ...) is stored here once,
 * together with its accessibility/metadata ({@code altText}, {@code fileSize},
 * {@code mimeType}, dimensions). Other entities reference a media asset through a
 * lazy {@code *_media_id} {@code @ManyToOne}, which keeps image metadata
 * normalized in a single place and avoids repeating those columns across tables.
 *
 * <p>{@code MediaAsset} is intentionally a lean shared root with <em>no</em>
 * inverse collections, to avoid circular references and accidental large fetches.
 */
@Entity
@Table(
        name = "media_assets",
        indexes = @Index(name = "idx_media_type", columnList = "media_type")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MediaAsset extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Relative storage path served under {@code /uploads/**}. */
    @NotBlank
    @Size(max = 255)
    @ToString.Include
    @Column(name = "file_path", nullable = false, length = 255)
    private String filePath;

    /** Original client file name (informational). */
    @Size(max = 255)
    @Column(name = "original_name", length = 255)
    private String originalName;

    /** Accessibility / SEO alternative text. */
    @Size(max = 255)
    @Column(name = "alt_text", length = 255)
    private String altText;

    /** MIME content type, e.g. {@code image/png}. */
    @NotBlank
    @Size(max = 100)
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    /** File size in bytes. */
    @NotNull
    @Positive
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /** Pixel width (images only; optional). */
    @Column(name = "width")
    private Integer width;

    /** Pixel height (images only; optional). */
    @Column(name = "height")
    private Integer height;

    /** High-level media classification. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaType mediaType = MediaType.IMAGE;
}
