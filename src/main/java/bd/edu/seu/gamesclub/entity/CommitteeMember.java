package bd.edu.seu.gamesclub.entity;

import bd.edu.seu.gamesclub.entity.enums.CommitteeType;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A member of either the Executive or Sub-Executive committee.
 *
 * <p>Both committees share this table; {@link CommitteeType} discriminates
 * between them. Students only view these records; admins manage them (CRUD).
 */
@Entity
@Table(
        name = "committee_members",
        indexes = @Index(name = "idx_committee_type_order", columnList = "committee_type, display_order")
)
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CommitteeMember extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /** Which committee this member belongs to. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Column(name = "committee_type", nullable = false, length = 20)
    private CommitteeType committeeType;

    /** Member full name. */
    @NotBlank
    @Size(max = 120)
    @ToString.Include
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    /** Academic department. */
    @NotBlank
    @Size(max = 100)
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    /** Admission batch. */
    @NotBlank
    @Size(max = 20)
    @Column(name = "batch", nullable = false, length = 20)
    private String batch;

    /** Position / designation held. */
    @NotBlank
    @Size(max = 80)
    @Column(name = "position", nullable = false, length = 80)
    private String position;

    /** Optional portrait photo from the media library. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_media_id", foreignKey = @ForeignKey(name = "fk_committee_photo"))
    private MediaAsset photo;

    /** Optional Facebook profile URL. */
    @Size(max = 255)
    @Column(name = "facebook_url", length = 255)
    private String facebookUrl;

    /** Optional LinkedIn profile URL. */
    @Size(max = 255)
    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    /** Committee session, e.g. {@code "2025-2026"}. */
    @Size(max = 20)
    @Column(name = "session_year", length = 20)
    private String sessionYear;

    /** Manual ordering for display. */
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    /** Whether the member is shown publicly. */
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
