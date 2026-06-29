package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.CommitteeMemberRequest;
import bd.edu.seu.gamesclub.dto.CommitteeMemberResponse;
import bd.edu.seu.gamesclub.entity.enums.CommitteeType;
import bd.edu.seu.gamesclub.service.CommitteeService;
import bd.edu.seu.gamesclub.service.MediaService;
import java.util.ArrayList;
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

/** Admin CRUD for Executive and Sub-Executive committee members. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommitteeController {

    private final CommitteeService committeeService;
    private final MediaService mediaService;

    public AdminCommitteeController(CommitteeService committeeService, MediaService mediaService) {
        this.committeeService = committeeService;
        this.mediaService = mediaService;
    }

    @GetMapping("/admin/committee")
    public String list(Model model) {
        List<CommitteeMemberResponse> members = new ArrayList<>();
        members.addAll(committeeService.getAllByType(CommitteeType.EXECUTIVE));
        members.addAll(committeeService.getAllByType(CommitteeType.SUB_EXECUTIVE));
        model.addAttribute("members", members);
        return "admin/committee/list";
    }

    @GetMapping("/admin/committee/new")
    public String createForm(Model model) {
        model.addAttribute("member", null);
        return "admin/committee/form";
    }

    @GetMapping("/admin/committee/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("member", committeeService.getById(id));
        return "admin/committee/form";
    }

    @PostMapping("/admin/committee/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String committeeType,
                      @RequestParam String name,
                      @RequestParam String department,
                      @RequestParam String batch,
                      @RequestParam String position,
                      @RequestParam(required = false) String facebookUrl,
                      @RequestParam(required = false) String linkedinUrl,
                      @RequestParam(required = false) String sessionYear,
                      @RequestParam(required = false) Integer displayOrder,
                      @RequestParam(defaultValue = "false") boolean active,
                      @RequestParam(required = false) Long currentPhotoMediaId,
                      @RequestParam(required = false) MultipartFile photoFile,
                      RedirectAttributes ra) {
        Long photoId = (photoFile != null && !photoFile.isEmpty())
                ? mediaService.store(photoFile, name).id() : currentPhotoMediaId;
        CommitteeMemberRequest req = new CommitteeMemberRequest(committeeType, name, department, batch,
                position, photoId, facebookUrl, linkedinUrl, sessionYear, displayOrder, active);
        if (id == null) {
            committeeService.create(req);
            ra.addFlashAttribute("successMessage", "Committee member created.");
        } else {
            committeeService.update(id, req);
            ra.addFlashAttribute("successMessage", "Committee member updated.");
        }
        return "redirect:/admin/committee";
    }

    @PostMapping("/admin/committee/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        committeeService.delete(id);
        ra.addFlashAttribute("successMessage", "Committee member deleted.");
        return "redirect:/admin/committee";
    }

    @PostMapping("/admin/committee/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(committeeService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected members deleted.");
        return "redirect:/admin/committee";
    }
}
