package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.SponsorRequest;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.SponsorService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin CRUD for sponsors. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminSponsorController {

    private final SponsorService sponsorService;
    private final MediaService mediaService;

    public AdminSponsorController(SponsorService sponsorService, MediaService mediaService) {
        this.sponsorService = sponsorService;
        this.mediaService = mediaService;
    }

    @GetMapping("/admin/sponsors")
    public String list(Model model) {
        model.addAttribute("sponsors", sponsorService.getAll());
        return "admin/sponsors/list";
    }

    @GetMapping("/admin/sponsors/new")
    public String createForm(Model model) {
        model.addAttribute("sponsor", null);
        return "admin/sponsors/form";
    }

    @GetMapping("/admin/sponsors/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("sponsor", sponsorService.getById(id));
        return "admin/sponsors/form";
    }

    @PostMapping("/admin/sponsors/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String name,
                      @RequestParam(required = false) String website,
                      @RequestParam(required = false) Integer displayOrder,
                      @RequestParam(defaultValue = "false") boolean active,
                      @RequestParam(required = false) Long currentLogoMediaId,
                      @RequestParam(required = false) MultipartFile logoFile,
                      RedirectAttributes ra) {
        Long logoId = (logoFile != null && !logoFile.isEmpty())
                ? mediaService.store(logoFile, name + " logo").id() : currentLogoMediaId;
        SponsorRequest req = new SponsorRequest(name, logoId, website, displayOrder, active);
        if (id == null) {
            sponsorService.create(req);
            ra.addFlashAttribute("successMessage", "Sponsor created.");
        } else {
            sponsorService.update(id, req);
            ra.addFlashAttribute("successMessage", "Sponsor updated.");
        }
        return "redirect:/admin/sponsors";
    }

    @PostMapping("/admin/sponsors/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        sponsorService.delete(id);
        ra.addFlashAttribute("successMessage", "Sponsor deleted.");
        return "redirect:/admin/sponsors";
    }

    @PostMapping("/admin/sponsors/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(sponsorService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected sponsors deleted.");
        return "redirect:/admin/sponsors";
    }
}
