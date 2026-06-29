package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.SocialLinkRequest;
import bd.edu.seu.gamesclub.exception.BusinessException;
import bd.edu.seu.gamesclub.service.SocialLinkService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin CRUD for social media links (unique platform). */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminSocialLinkController {

    private final SocialLinkService socialLinkService;

    public AdminSocialLinkController(SocialLinkService socialLinkService) {
        this.socialLinkService = socialLinkService;
    }

    @GetMapping("/admin/social-links")
    public String list(Model model) {
        model.addAttribute("links", socialLinkService.getAll());
        return "admin/social/list";
    }

    @GetMapping("/admin/social-links/new")
    public String createForm(Model model) {
        model.addAttribute("link", null);
        return "admin/social/form";
    }

    @GetMapping("/admin/social-links/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("link", socialLinkService.getById(id));
        return "admin/social/form";
    }

    @PostMapping("/admin/social-links/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String platform,
                      @RequestParam String url,
                      @RequestParam(required = false) String iconClass,
                      @RequestParam(required = false) Integer displayOrder,
                      @RequestParam(defaultValue = "false") boolean active,
                      RedirectAttributes ra) {
        SocialLinkRequest req = new SocialLinkRequest(platform, url, iconClass, displayOrder, active);
        try {
            if (id == null) {
                socialLinkService.create(req);
                ra.addFlashAttribute("successMessage", "Social link created.");
            } else {
                socialLinkService.update(id, req);
                ra.addFlashAttribute("successMessage", "Social link updated.");
            }
        } catch (BusinessException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return id == null ? "redirect:/admin/social-links/new" : "redirect:/admin/social-links/" + id + "/edit";
        }
        return "redirect:/admin/social-links";
    }

    @PostMapping("/admin/social-links/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        socialLinkService.delete(id);
        ra.addFlashAttribute("successMessage", "Social link deleted.");
        return "redirect:/admin/social-links";
    }

    @PostMapping("/admin/social-links/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(socialLinkService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected links deleted.");
        return "redirect:/admin/social-links";
    }
}
