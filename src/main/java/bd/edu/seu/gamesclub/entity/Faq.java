package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A frequently-asked question shown on the public site and managed by admins.
 */
@Entity
@Table(
        name = "faqs",
        indexes = @Index(name = "idx_faqs_active_order", columnList = "is_active, display_order")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Faq extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** The question text. */
    @NotBlank
    @Size(max = 255)
    @ToString.Include
    @Column(name = "question", nullable = false, length = 255)
    private String question;

    /** The answer text. */
    @NotBlank
    @Lob
    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    /** Manual ordering for display. */
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    /** Whether the FAQ is shown publicly. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
