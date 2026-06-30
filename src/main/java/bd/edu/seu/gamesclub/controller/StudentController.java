package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.dto.StudentResponse;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.service.EventService;
import bd.edu.seu.gamesclub.service.MembershipService;
import bd.edu.seu.gamesclub.service.NoticeService;
import bd.edu.seu.gamesclub.service.StudentService;
import java.security.Principal;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Student area (dashboard). All routes require ROLE_STUDENT (enforced by security). */
@Controller
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;
    private final MembershipService membershipService;
    private final EventService eventService;
    private final NoticeService noticeService;

    public StudentController(StudentService studentService, MembershipService membershipService,
                            EventService eventService, NoticeService noticeService) {
        this.studentService = studentService;
        this.membershipService = membershipService;
        this.eventService = eventService;
        this.noticeService = noticeService;
    }

    @GetMapping("/student/dashboard")
    public String dashboard(Principal principal, Model model) {
        String email = principal.getName();
        StudentResponse profile = null;
        try {
            profile = studentService.getByEmail(email);
        } catch (ResourceNotFoundException ignored) {
            // profile may not exist yet; template degrades gracefully
        }
        model.addAttribute("studentProfile", profile);
        model.addAttribute("currentUser", Map.of("email", email));
        model.addAttribute("membershipOpen", membershipService.isOpen());
        model.addAttribute("myApplication", membershipService.getMyApplication(email));
        model.addAttribute("stats", Map.of(
                "upcomingEvents", eventService.getUpcoming().size(),
                "activeNotices", (int) noticeService.getPublished(PageRequest.of(0, 1)).getTotalElements()
        ));
        return "student/dashboard";
    }

    @GetMapping("/student/profile")
    public String profile(Principal principal, Model model) {
        String email = principal.getName();
        StudentResponse profile = null;
        try {
            profile = studentService.getByEmail(email);
        } catch (ResourceNotFoundException ignored) {
            // profile may not exist yet; template degrades gracefully
        }
        model.addAttribute("studentProfile", profile);
        model.addAttribute("currentUser", Map.of("email", email));
        return "student/profile";
    }

    @GetMapping("/student/notices")
    public String notices() {
        return "redirect:/news";
    }
}
