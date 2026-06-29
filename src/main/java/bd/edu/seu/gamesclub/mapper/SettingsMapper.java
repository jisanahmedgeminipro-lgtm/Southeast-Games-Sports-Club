package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.ClubSettingsRequest;
import bd.edu.seu.gamesclub.dto.ClubSettingsResponse;
import bd.edu.seu.gamesclub.dto.SystemSettingsRequest;
import bd.edu.seu.gamesclub.dto.SystemSettingsResponse;
import bd.edu.seu.gamesclub.entity.ClubSettings;
import bd.edu.seu.gamesclub.entity.SystemSettings;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for the singleton club and system settings. */
public final class SettingsMapper {

    private SettingsMapper() {
    }

    public static ClubSettingsResponse toResponse(ClubSettings c) {
        if (c == null) {
            return null;
        }
        return new ClubSettingsResponse(
                c.getId(), c.getUniversityName(), c.getClubName(), MediaUrls.url(c.getLogo()),
                c.getEstablishedYear(), c.getMotto(), c.getAboutClub(), c.getAddress(),
                c.getPhone(), c.getEmail(), c.getGoogleMapLink(),
                c.getLogo() != null ? c.getLogo().getId() : null
        );
    }

    public static void apply(ClubSettings c, ClubSettingsRequest r) {
        c.setUniversityName(r.universityName());
        c.setClubName(r.clubName());
        c.setEstablishedYear(r.establishedYear());
        c.setMotto(r.motto());
        c.setAboutClub(r.aboutClub());
        c.setAddress(r.address());
        c.setPhone(r.phone());
        c.setEmail(r.email());
        c.setGoogleMapLink(r.googleMapLink());
    }

    public static SystemSettingsResponse toResponse(SystemSettings s) {
        if (s == null) {
            return null;
        }
        return new SystemSettingsResponse(
                s.getId(), s.getSiteTitle(), MediaUrls.url(s.getFavicon()), s.getThemeColor(),
                s.getFooterCopyright(), s.getSmtpSenderName(), s.isMaintenanceMode(),
                s.getFavicon() != null ? s.getFavicon().getId() : null
        );
    }

    public static void apply(SystemSettings s, SystemSettingsRequest r) {
        s.setSiteTitle(r.siteTitle());
        s.setThemeColor(r.themeColor());
        s.setFooterCopyright(r.footerCopyright());
        s.setSmtpSenderName(r.smtpSenderName());
        if (r.maintenanceMode() != null) {
            s.setMaintenanceMode(r.maintenanceMode());
        }
    }
}
