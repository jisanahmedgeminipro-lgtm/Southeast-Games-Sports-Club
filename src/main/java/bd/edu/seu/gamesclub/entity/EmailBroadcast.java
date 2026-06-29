package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.BroadcastStatus;
import bd.edu.seu.gamesclub.entity.enums.BroadcastTarget;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Header record for an admin email broadcast campaign.
 *
 * <p>The sending admin is captured by the inherited {@code created_by} audit
 * field. The owned per-recipient delivery log ({@link EmailBroadcastRecipient})
 * is modelled as a {@code LAZY} collection with {@code cascade = ALL} and
 * {@code orphanRemoval = true}. {@code recipientCount} is denormalized for fast
 * dashboard reads.
 */
@Entity
@Table(
        name = "email_broadcasts",
        indexes = @Index(name = "idx_email_broadcasts_sent_at", columnList = "sent_at")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EmailBroadcast extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Email subject. */
    @NotBlank
    @Size(max = 200)
    @ToString.Include
    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    /** Email body (HTML allowed). */
    @NotBlank
    @Lob
    @Column(name = "body", nullable = false, columnDefinition = "LONGTEXT")
    private String body;

    /** Which audience was targeted. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "target_type", nullable = false, length = 20)
    private BroadcastTarget targetType;

    /** Number of recipients the campaign was sent to. */
    @PositiveOrZero
    @Column(name = "recipient_count", nullable = false)
    private int recipientCount = 0;

    /** Overall dispatch status. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "status", nullable = false, length = 20)
    private BroadcastStatus status = BroadcastStatus.PENDING;

    /** When dispatch completed. */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /** Per-recipient delivery log (owned aggregate). */
    @OneToMany(
            mappedBy = "broadcast",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<EmailBroadcastRecipient> recipients = new ArrayList<>();
}
