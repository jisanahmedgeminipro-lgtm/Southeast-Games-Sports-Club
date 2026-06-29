package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.HeroSliderRequest;
import bd.edu.seu.gamesclub.service.HeroSliderService;
import bd.edu.seu.gamesclub.service.MediaService;
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

/** Admin CRUD for landing hero slides. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminHeroController {

    private final HeroSliderService heroSliderService;
    private final MediaService mediaService;

    public AdminHeroController(HeroSliderService heroSliderService, MediaService mediaService) {
        this.heroSliderService = heroSliderService;
        this.mediaService = mediaService;
    }

    @GetMapping("/admin/hero-sliders")
    public String list(Model model) {
        model.addAttribute("sliders", heroSliderService.getAll());
        return "admin/hero/list";
    }

    @GetMapping("/admin/hero-sliders/new")
    public String createForm(Model model) {
        model.addAttribute("slider", null);
        return "admin/hero/form";
    }

    @GetMapping("/admin/hero-sliders/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("slider", heroSliderService.getById(id));
        return "admin/hero/form";
    }

    @PostMapping("/admin/hero-sliders/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam(required = false) String title,
                      @RequestParam(required = false) String subtitle,
                      @RequestParam(required = false) String description,
                      @RequestParam(required = false) String buttonText,
                      @RequestParam(required = false) String buttonUrl,
                      @RequestParam(required = false) Integer displayOrder,
                      @RequestParam(defaultValue = "false") boolean active,
                      @RequestParam(required = false) Long currentBackgroundMediaId,
                      @RequestParam(required = false) MultipartFile backgroundFile,
                      RedirectAttributes ra) {
        Long mediaId = (backgroundFile != null && !backgroundFile.isEmpty())
                ? mediaService.store(backgroundFile, title).id() : currentBackgroundMediaId;
        HeroSliderRequest req = new HeroSliderRequest(title, subtitle, description, buttonText,
                buttonUrl, mediaId, displayOrder, active);
        if (id == null) {
            heroSliderService.create(req);
            ra.addFlashAttribute("successMessage", "Hero slide created.");
        } else {
            heroSliderService.update(id, req);
            ra.addFlashAttribute("successMessage", "Hero slide updated.");
        }
        return "redirect:/admin/hero-sliders";
    }

    @PostMapping("/admin/hero-sliders/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        heroSliderService.delete(id);
        ra.addFlashAttribute("successMessage", "Hero slide deleted.");
        return "redirect:/admin/hero-sliders";
    }

    @PostMapping("/admin/hero-sliders/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(heroSliderService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected slides deleted.");
        return "redirect:/admin/hero-sliders";
    }
}
