package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.AchievementRequest;
import bd.edu.seu.gamesclub.service.AchievementService;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.SportService;
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

/** Admin CRUD for achievements. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminAchievementController {

    private final AchievementService achievementService;
    private final SportService sportService;
    private final MediaService mediaService;

    public AdminAchievementController(AchievementService achievementService, SportService sportService,
                                     MediaService mediaService) {
        this.achievementService = achievementService;
        this.sportService = sportService;
        this.mediaService = mediaService;
    }

    @GetMapping("/admin/achievements")
    public String list(Model model) {
        model.addAttribute("achievements", achievementService.getAll());
        return "admin/achievements/list";
    }

    @GetMapping("/admin/achievements/new")
    public String createForm(Model model) {
        model.addAttribute("achievement", null);
        model.addAttribute("sports", sportService.getAll());
        return "admin/achievements/form";
    }

    @GetMapping("/admin/achievements/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("achievement", achievementService.getById(id));
        model.addAttribute("sports", sportService.getAll());
        return "admin/achievements/form";
    }

    @PostMapping("/admin/achievements/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String title,
                      @RequestParam Short achievementYear,
                      @RequestParam(required = false) String description,
                      @RequestParam(required = false) Long sportId,
                      @RequestParam(required = false) Integer displayOrder,
                      @RequestParam(required = false) Long currentImageMediaId,
                      @RequestParam(required = false) MultipartFile imageFile,
                      RedirectAttributes ra) {
        Long imageId = (imageFile != null && !imageFile.isEmpty())
                ? mediaService.store(imageFile, title).id() : currentImageMediaId;
        AchievementRequest req = new AchievementRequest(title, achievementYear, description, imageId, sportId, displayOrder);
        if (id == null) {
            achievementService.create(req);
            ra.addFlashAttribute("successMessage", "Achievement created.");
        } else {
            achievementService.update(id, req);
            ra.addFlashAttribute("successMessage", "Achievement updated.");
        }
        return "redirect:/admin/achievements";
    }

    @PostMapping("/admin/achievements/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        achievementService.delete(id);
        ra.addFlashAttribute("successMessage", "Achievement deleted.");
        return "redirect:/admin/achievements";
    }

    @PostMapping("/admin/achievements/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(achievementService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected achievements deleted.");
        return "redirect:/admin/achievements";
    }
}
