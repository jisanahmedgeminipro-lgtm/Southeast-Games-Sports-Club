package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.NoticeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
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
 * A notice-board entry (practice, tournament, holiday or general notice).
 */
@Entity
@Table(
        name = "notices",
        indexes = {
                @Index(name = "idx_notices_type", columnList = "notice_type"),
                @Index(name = "idx_notices_publish_date", columnList = "publish_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Notice extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Notice title. */
    @NotBlank
    @Size(max = 180)
    @ToString.Include
    @Column(name = "title", nullable = false, length = 180)
    private String title;

    /** Notice body. */
    @NotBlank
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Notice category. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "notice_type", nullable = false, length = 20)
    private NoticeType noticeType = NoticeType.GENERAL;

    /** Publication date (nullable). */
    @Column(name = "publish_date")
    private LocalDate publishDate;

    /** Optional expiry date after which it is hidden. */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /** Whether the notice is pinned to the top. */
    @Column(name = "is_pinned", nullable = false)
    private boolean pinned = false;

    /** Whether the notice is visible. */
    @Column(name = "is_published", nullable = false)
    private boolean published = true;
}
