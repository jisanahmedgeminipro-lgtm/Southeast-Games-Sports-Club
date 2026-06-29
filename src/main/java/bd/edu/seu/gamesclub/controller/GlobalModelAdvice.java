package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.dto.ClubSettingsResponse;
import bd.edu.seu.gamesclub.dto.SocialLinkResponse;
import bd.edu.seu.gamesclub.dto.SportResponse;
import bd.edu.seu.gamesclub.dto.SystemSettingsResponse;
import bd.edu.seu.gamesclub.service.ClubSettingsService;
import bd.edu.seu.gamesclub.service.SocialLinkService;
import bd.edu.seu.gamesclub.service.SportService;
import bd.edu.seu.gamesclub.service.SystemSettingsService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Supplies the data the shared navbar/footer fragments need on <em>every</em>
 * rendered page (club identity, social links, footer sports list, site settings),
 * so individual controllers don't have to repeat it.
 */
@Slf4j
@ControllerAdvice(basePackages = "bd.edu.seu.gamesclub.controller")
public class GlobalModelAdvice {

    private final ClubSettingsService clubSettingsService;
    private final SocialLinkService socialLinkService;
    private final SportService sportService;
    private final SystemSettingsService systemSettingsService;

    public GlobalModelAdvice(ClubSettingsService clubSettingsService,
                             SocialLinkService socialLinkService,
                             SportService sportService,
                             SystemSettingsService systemSettingsService) {
        this.clubSettingsService = clubSettingsService;
        this.socialLinkService = socialLinkService;
        this.sportService = sportService;
        this.systemSettingsService = systemSettingsService;
    }

    @ModelAttribute("clubSettings")
    public ClubSettingsResponse clubSettings() {
        return safe(clubSettingsService::get);
    }

    @ModelAttribute("socialLinks")
    public List<SocialLinkResponse> socialLinks() {
        List<SocialLinkResponse> links = safe(socialLinkService::getActive);
        return links != null ? links : List.of();
    }

    @ModelAttribute("footerSports")
    public List<SportResponse> footerSports() {
        List<SportResponse> sports = safe(sportService::getActive);
        return sports != null ? sports : List.of();
    }

    @ModelAttribute("systemSettings")
    public SystemSettingsResponse systemSettings() {
        return safe(systemSettingsService::get);
    }

    /** Never let a fragment data lookup break page rendering. */
    private <T> T safe(java.util.function.Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            log.debug("Global model attribute unavailable: {}", ex.getMessage());
            return null;
        }
    }
}
