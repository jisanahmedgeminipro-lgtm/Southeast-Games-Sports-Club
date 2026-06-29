package bd.edu.seu.gamesclub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Singleton holding application/site technical configuration, editable by admins
 * from the dashboard.
 *
 * <p>Exactly one row exists, with {@code id = 1}. Distinct from
 * {@link ClubSettings}: this covers the running site (title, favicon, theme,
 * footer, SMTP sender name, maintenance mode) rather than the club's identity.
 */
@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SystemSettings extends Auditable {

    /** Fixed singleton primary key (always {@code 1}). */
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "id", nullable = false)
    private Long id = 1L;

    /** Browser/site title. */
    @NotBlank
    @Size(max = 160)
    @ToString.Include
    @Column(name = "site_title", nullable = false, length = 160)
    private String siteTitle;

    /** Optional favicon (media library reference). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favicon_media_id", foreignKey = @ForeignKey(name = "fk_system_settings_favicon"))
    private MediaAsset favicon;

    /** Primary theme color as a hex string, e.g. {@code #0F766E}. */
    @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6})$", message = "must be a valid hex color")
    @Size(max = 20)
    @Column(name = "theme_color", length = 20)
    private String themeColor;

    /** Footer copyright line. */
    @Size(max = 255)
    @Column(name = "footer_copyright", length = 255)
    private String footerCopyright;

    /** Display name used as the SMTP "from" name. */
    @Size(max = 120)
    @Column(name = "smtp_sender_name", length = 120)
    private String smtpSenderName;

    /** When true, the public site shows a maintenance page. */
    @Column(name = "is_maintenance_mode", nullable = false)
    private boolean maintenanceMode = false;
}
