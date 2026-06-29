package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.ClubSettingsRequest;
import bd.edu.seu.gamesclub.dto.SystemSettingsRequest;
import bd.edu.seu.gamesclub.service.ClubSettingsService;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.SystemSettingsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin management of the singleton club and system settings. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingsController {

    private final ClubSettingsService clubSettingsService;
    private final SystemSettingsService systemSettingsService;
    private final MediaService mediaService;

    public AdminSettingsController(ClubSettingsService clubSettingsService,
                                  SystemSettingsService systemSettingsService,
                                  MediaService mediaService) {
        this.clubSettingsService = clubSettingsService;
        this.systemSettingsService = systemSettingsService;
        this.mediaService = mediaService;
    }

    /* ----------------------------- Club ----------------------------- */
    @GetMapping("/admin/settings/club")
    public String clubForm(Model model) {
        model.addAttribute("settings", clubSettingsService.get());
        return "admin/settings/club";
    }

    @PostMapping("/admin/settings/club")
    public String saveClub(@RequestParam String universityName,
                          @RequestParam String clubName,
                          @RequestParam(required = false) Short establishedYear,
                          @RequestParam(required = false) String motto,
                          @RequestParam(required = false) String aboutClub,
                          @RequestParam(required = false) String address,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String googleMapLink,
                          @RequestParam(required = false) Long currentLogoMediaId,
                          @RequestParam(required = false) MultipartFile logoFile,
                          RedirectAttributes ra) {
        Long logoMediaId = resolveMedia(logoFile, currentLogoMediaId, clubName + " logo");
        clubSettingsService.update(new ClubSettingsRequest(universityName, clubName, logoMediaId,
                establishedYear, motto, aboutClub, address, phone, email, googleMapLink));
        ra.addFlashAttribute("successMessage", "Club settings saved.");
        return "redirect:/admin/settings/club";
    }

    /* ----------------------------- System ----------------------------- */
    @GetMapping("/admin/settings/system")
    public String systemForm(Model model) {
        model.addAttribute("settings", systemSettingsService.get());
        return "admin/settings/system";
    }

    @PostMapping("/admin/settings/system")
    public String saveSystem(@RequestParam String siteTitle,
                            @RequestParam(required = false) String themeColor,
                            @RequestParam(required = false) String footerCopyright,
                            @RequestParam(required = false) String smtpSenderName,
                            @RequestParam(required = false, defaultValue = "false") boolean maintenanceMode,
                            @RequestParam(required = false) Long currentFaviconMediaId,
                            @RequestParam(required = false) MultipartFile faviconFile,
                            RedirectAttributes ra) {
        Long faviconMediaId = resolveMedia(faviconFile, currentFaviconMediaId, "favicon");
        systemSettingsService.update(new SystemSettingsRequest(siteTitle, faviconMediaId, themeColor,
                footerCopyright, smtpSenderName, maintenanceMode));
        ra.addFlashAttribute("successMessage", "System settings saved.");
        return "redirect:/admin/settings/system";
    }

    /** Stores a newly uploaded image, or keeps the current one when no file is sent. */
    private Long resolveMedia(MultipartFile file, Long current, String alt) {
        if (file != null && !file.isEmpty()) {
            return mediaService.store(file, alt).id();
        }
        return current;
    }
}
