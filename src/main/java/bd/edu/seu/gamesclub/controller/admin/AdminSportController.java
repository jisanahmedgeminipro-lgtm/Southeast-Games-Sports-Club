package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.SportRequest;
import bd.edu.seu.gamesclub.exception.BusinessException;
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

/** Admin CRUD for sports (unique names). */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminSportController {

    private final SportService sportService;
    private final MediaService mediaService;

    public AdminSportController(SportService sportService, MediaService mediaService) {
        this.sportService = sportService;
        this.mediaService = mediaService;
    }

    @GetMapping("/admin/sports")
    public String list(Model model) {
        model.addAttribute("sports", sportService.getAll());
        return "admin/sports/list";
    }

    @GetMapping("/admin/sports/new")
    public String createForm(Model model) {
        model.addAttribute("sport", null);
        return "admin/sports/form";
    }

    @GetMapping("/admin/sports/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("sport", sportService.getById(id));
        return "admin/sports/form";
    }

    @PostMapping("/admin/sports/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String name,
                      @RequestParam(required = false) String description,
                      @RequestParam(required = false) Integer displayOrder,
                      @RequestParam(defaultValue = "false") boolean active,
                      @RequestParam(required = false) Long currentIconMediaId,
                      @RequestParam(required = false) Long currentImageMediaId,
                      @RequestParam(required = false) MultipartFile iconFile,
                      @RequestParam(required = false) MultipartFile imageFile,
                      RedirectAttributes ra) {
        Long iconId = (iconFile != null && !iconFile.isEmpty())
                ? mediaService.store(iconFile, name + " icon").id() : currentIconMediaId;
        Long imageId = (imageFile != null && !imageFile.isEmpty())
                ? mediaService.store(imageFile, name).id() : currentImageMediaId;
        SportRequest req = new SportRequest(name, description, iconId, imageId, displayOrder, active);
        try {
            if (id == null) {
                sportService.create(req);
                ra.addFlashAttribute("successMessage", "Sport created.");
            } else {
                sportService.update(id, req);
                ra.addFlashAttribute("successMessage", "Sport updated.");
            }
        } catch (BusinessException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return id == null ? "redirect:/admin/sports/new" : "redirect:/admin/sports/" + id + "/edit";
        }
        return "redirect:/admin/sports";
    }

    @PostMapping("/admin/sports/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        sportService.delete(id);
        ra.addFlashAttribute("successMessage", "Sport deleted.");
        return "redirect:/admin/sports";
    }

    @PostMapping("/admin/sports/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(sportService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected sports deleted.");
        return "redirect:/admin/sports";
    }
}
