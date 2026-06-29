package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.PublishStatus;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A news / blog article published by the club.
 */
@Entity
@Table(
        name = "news",
        uniqueConstraints = @UniqueConstraint(name = "uq_news_slug", columnNames = "slug"),
        indexes = {
                @Index(name = "idx_news_publish_date", columnList = "publish_date"),
                @Index(name = "idx_news_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class News extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Article title. */
    @NotBlank
    @Size(max = 180)
    @ToString.Include
    @Column(name = "title", nullable = false, length = 180)
    private String title;

    /** Unique URL slug. */
    @NotBlank
    @Size(max = 200)
    @Column(name = "slug", nullable = false, length = 200)
    private String slug;

    /** Article body. */
    @NotBlank
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Optional featured image. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_media_id", foreignKey = @ForeignKey(name = "fk_news_image"))
    private MediaAsset image;

    /** Publication date (nullable while in draft). */
    @Column(name = "publish_date")
    private LocalDate publishDate;

    /** Draft / published state. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "status", nullable = false, length = 20)
    private PublishStatus status = PublishStatus.DRAFT;
}
