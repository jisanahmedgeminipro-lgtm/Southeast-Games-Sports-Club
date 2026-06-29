package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Singleton holding the club's identity and contact details, editable by admins
 * from the dashboard (no source changes required).
 *
 * <p>Exactly one row exists, with {@code id = 1} (enforced by a DB CHECK and by
 * always loading/saving id {@code 1L}). Social media URLs are deliberately
 * <strong>not</strong> stored here - {@link SocialLink} is the single source of
 * truth for those.
 */
@Entity
@Table(name = "club_settings")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ClubSettings extends Auditable {

    /** Fixed singleton primary key (always {@code 1}). */
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "id", nullable = false)
    private Long id = 1L;

    /** University name. */
    @NotBlank
    @Size(max = 160)
    @ToString.Include
    @Column(name = "university_name", nullable = false, length = 160)
    private String universityName;

    /** Club name. */
    @NotBlank
    @Size(max = 160)
    @ToString.Include
    @Column(name = "club_name", nullable = false, length = 160)
    private String clubName;

    /** Optional club logo (media library reference). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_media_id", foreignKey = @ForeignKey(name = "fk_club_settings_logo"))
    private MediaAsset logo;

    /** Year the club was established. */
    @Min(1900)
    @Max(2100)
    @Column(name = "established_year")
    private Short establishedYear;

    /** Short motto / tagline. */
    @Size(max = 255)
    @Column(name = "motto", length = 255)
    private String motto;

    /** Long "about the club" text. */
    @Lob
    @Column(name = "about_club", columnDefinition = "TEXT")
    private String aboutClub;

    /** Postal address. */
    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    /** Contact phone. */
    @Size(max = 40)
    @Column(name = "phone", length = 40)
    private String phone;

    /** Contact email. */
    @Email
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    /** Google Maps embed / share link. */
    @Size(max = 500)
    @Column(name = "google_map_link", length = 500)
    private String googleMapLink;
}
