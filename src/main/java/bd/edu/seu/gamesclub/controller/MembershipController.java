package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.exception.BusinessException;
import bd.edu.seu.gamesclub.service.MembershipService;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Student-facing membership actions. Admin period management lives in the admin
 * area; the public membership info page is served by {@link HomeController}.
 */
@Controller
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/student/membership")
    @PreAuthorize("hasRole('STUDENT')")
    public String membership(Principal principal, Model model) {
        model.addAttribute("membershipOpen", membershipService.isOpen());
        model.addAttribute("myApplication", membershipService.getMyApplication(principal.getName()));
        model.addAttribute("currentPeriod", membershipService.getCurrentPeriod());
        return "student/membership";
    }

    @PostMapping("/student/membership/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public String apply(Principal principal, RedirectAttributes ra) {
        try {
            membershipService.apply(principal.getName());
            ra.addFlashAttribute("successMessage", "Your membership application has been submitted.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/dashboard";
    }
}
