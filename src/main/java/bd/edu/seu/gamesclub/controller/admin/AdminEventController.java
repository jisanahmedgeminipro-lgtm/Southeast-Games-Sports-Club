package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.EventRequest;
import bd.edu.seu.gamesclub.service.EventService;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.SportService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin CRUD for events. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {

    private final EventService eventService;
    private final SportService sportService;
    private final MediaService mediaService;

    public AdminEventController(EventService eventService, SportService sportService, MediaService mediaService) {
        this.eventService = eventService;
        this.sportService = sportService;
        this.mediaService = mediaService;
    }

    @GetMapping("/admin/events")
    public String list(Model model) {
        model.addAttribute("events", eventService.getPage(null, PageRequest.of(0, 500)).getContent());
        return "admin/events/list";
    }

    @GetMapping("/admin/events/new")
    public String createForm(Model model) {
        model.addAttribute("event", null);
        model.addAttribute("sports", sportService.getAll());
        return "admin/events/form";
    }

    @GetMapping("/admin/events/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.getById(id));
        model.addAttribute("sports", sportService.getAll());
        return "admin/events/form";
    }

    @PostMapping("/admin/events/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String title,
                      @RequestParam(required = false) String description,
                      @RequestParam(required = false) String venue,
                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime eventTime,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registrationDeadline,
                      @RequestParam(required = false) String status,
                      @RequestParam(required = false) Long sportId,
                      @RequestParam(required = false) Long currentBannerMediaId,
                      @RequestParam(required = false) MultipartFile bannerFile,
                      RedirectAttributes ra) {
        Long bannerId = (bannerFile != null && !bannerFile.isEmpty())
                ? mediaService.store(bannerFile, title).id() : currentBannerMediaId;
        EventRequest req = new EventRequest(title, description, bannerId, venue, eventDate, eventTime,
                registrationDeadline, status, sportId);
        if (id == null) {
            eventService.create(req);
            ra.addFlashAttribute("successMessage", "Event created.");
        } else {
            eventService.update(id, req);
            ra.addFlashAttribute("successMessage", "Event updated.");
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/admin/events/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        eventService.delete(id);
        ra.addFlashAttribute("successMessage", "Event deleted.");
        return "redirect:/admin/events";
    }

    @PostMapping("/admin/events/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(eventService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected events deleted.");
        return "redirect:/admin/events";
    }
}
