package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.MembershipPeriodRequest;
import bd.edu.seu.gamesclub.dto.MembershipPeriodResponse;
import bd.edu.seu.gamesclub.exception.BusinessException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.service.MembershipService;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin management of membership periods and applications. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminMembershipController {

    private final MembershipService membershipService;

    public AdminMembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    /* ----------------------------- Periods ----------------------------- */
    @GetMapping("/admin/membership")
    public String periods(Model model) {
        model.addAttribute("periods", membershipService.getAllPeriods());
        return "admin/membership/list";
    }

    @GetMapping("/admin/membership/new")
    public String createForm(Model model) {
        model.addAttribute("period", null);
        return "admin/membership/form";
    }

    @GetMapping("/admin/membership/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("period", findPeriod(id));
        return "admin/membership/form";
    }

    @PostMapping("/admin/membership/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String title,
                      @RequestParam(required = false) String announcement,
                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate openingDate,
                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate closingDate,
                      RedirectAttributes ra) {
        MembershipPeriodRequest req = new MembershipPeriodRequest(title, announcement, openingDate, closingDate);
        try {
            if (id == null) {
                membershipService.createPeriod(req);
                ra.addFlashAttribute("successMessage", "Membership period created.");
            } else {
                membershipService.updatePeriod(id, req);
                ra.addFlashAttribute("successMessage", "Membership period updated.");
            }
        } catch (BusinessException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return id == null ? "redirect:/admin/membership/new" : "redirect:/admin/membership/" + id + "/edit";
        }
        return "redirect:/admin/membership";
    }

    @PostMapping("/admin/membership/{id}/open")
    public String open(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        membershipService.open(id, principal.getName());
        ra.addFlashAttribute("successMessage", "Membership opened and students notified.");
        return "redirect:/admin/membership";
    }

    @PostMapping("/admin/membership/{id}/close")
    public String close(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        membershipService.close(id, principal.getName());
        ra.addFlashAttribute("successMessage", "Membership closed.");
        return "redirect:/admin/membership";
    }

    /* ----------------------------- Applications ----------------------------- */
    @GetMapping("/admin/applications")
    public String applications(@RequestParam(required = false) String status, Model model) {
        model.addAttribute("applications", membershipService.getApplications(status));
        model.addAttribute("statusFilter", status);
        return "admin/applications/list";
    }

    @PostMapping("/admin/applications/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam(required = false) String remarks,
                         Principal principal, RedirectAttributes ra) {
        membershipService.review(id, true, remarks, principal.getName());
        ra.addFlashAttribute("successMessage", "Application approved.");
        return "redirect:/admin/applications";
    }

    @PostMapping("/admin/applications/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(required = false) String remarks,
                        Principal principal, RedirectAttributes ra) {
        membershipService.review(id, false, remarks, principal.getName());
        ra.addFlashAttribute("successMessage", "Application rejected.");
        return "redirect:/admin/applications";
    }

    private MembershipPeriodResponse findPeriod(Long id) {
        return membershipService.getAllPeriods().stream()
                .filter(p -> p.id().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPeriod", "id", id));
    }
}
