package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.DeliveryStatus;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Per-recipient delivery record for an {@link EmailBroadcast}.
 *
 * <p>{@code recipientEmail} is a point-in-time snapshot of the address actually
 * mailed (kept even if the {@link User} is later removed). A unique constraint on
 * {@code (broadcast_id, recipient_user_id)} prevents duplicate sends. Deleting
 * the parent broadcast cascades here.
 */
@Entity
@Table(
        name = "email_broadcast_recipients",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_broadcast_recipient",
                columnNames = {"broadcast_id", "recipient_user_id"}
        ),
        indexes = @Index(name = "idx_broadcast_recipients_broadcast", columnList = "broadcast_id")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EmailBroadcastRecipient extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Parent broadcast campaign. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "broadcast_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_broadcast_recipients_broadcast")
    )
    private EmailBroadcast broadcast;

    /** The targeted user (nullable if later deleted). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recipient_user_id",
            foreignKey = @ForeignKey(name = "fk_broadcast_recipients_user")
    )
    private User recipient;

    /** Snapshot of the email address actually used. */
    @NotBlank
    @Email
    @Size(max = 150)
    @ToString.Include
    @Column(name = "recipient_email", nullable = false, length = 150)
    private String recipientEmail;

    /** Delivery outcome for this recipient. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "delivery_status", nullable = false, length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    /** Failure detail, when delivery failed. */
    @Size(max = 255)
    @Column(name = "error_message", length = 255)
    private String errorMessage;

    /** When this individual email was sent. */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
