package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.dto.StudentResponse;
import bd.edu.seu.gamesclub.dto.StudentUpdateRequest;
import bd.edu.seu.gamesclub.exception.BusinessException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.service.EventService;
import bd.edu.seu.gamesclub.service.MediaService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Student area (dashboard + profile). All routes require ROLE_STUDENT (enforced by security). */
@Controller
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;
    private final MembershipService membershipService;
    private final EventService eventService;
    private final NoticeService noticeService;
    private final MediaService mediaService;

    public StudentController(StudentService studentService, MembershipService membershipService,
                            EventService eventService, NoticeService noticeService,
                            MediaService mediaService) {
        this.studentService = studentService;
        this.membershipService = membershipService;
        this.eventService = eventService;
        this.noticeService = noticeService;
        this.mediaService = mediaService;
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

    @GetMapping("/student/profile/edit")
    public String editProfile(Principal principal, Model model) {
        String email = principal.getName();
        StudentResponse profile;
        try {
            profile = studentService.getByEmail(email);
        } catch (ResourceNotFoundException ex) {
            return "redirect:/student/dashboard";
        }
        model.addAttribute("studentProfile", profile);
        model.addAttribute("currentUser", Map.of("email", email));
        return "student/settings";
    }

    @PostMapping("/student/profile")
    public String updateProfile(Principal principal,
                                @RequestParam String fullName,
                                @RequestParam String department,
                                @RequestParam String batch,
                                @RequestParam String semester,
                                @RequestParam String phone,
                                @RequestParam String gender,
                                @RequestParam(required = false) MultipartFile profileImage,
                                RedirectAttributes ra) {
        String email = principal.getName();
        try {
            // A new image (if any) replaces the picture; otherwise the existing one is kept.
            Long mediaId = (profileImage != null && !profileImage.isEmpty())
                    ? mediaService.store(profileImage, fullName).id() : null;
            studentService.updateOwnProfile(email,
                    new StudentUpdateRequest(fullName, department, batch, semester, phone, gender, mediaId));
            ra.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/student/notices")
    public String notices() {
        return "redirect:/news";
    }
}
