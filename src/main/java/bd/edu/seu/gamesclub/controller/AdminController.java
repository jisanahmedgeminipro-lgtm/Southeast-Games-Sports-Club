package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.dto.StudentResponse;
import bd.edu.seu.gamesclub.service.ActivityLogService;
import bd.edu.seu.gamesclub.service.ContactService;
import bd.edu.seu.gamesclub.service.DashboardService;
import bd.edu.seu.gamesclub.service.MembershipService;
import bd.edu.seu.gamesclub.service.StudentService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin area: dashboard and student management. All routes require ROLE_ADMIN. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DashboardService dashboardService;
    private final MembershipService membershipService;
    private final ActivityLogService activityLogService;
    private final ContactService contactService;
    private final StudentService studentService;

    public AdminController(DashboardService dashboardService, MembershipService membershipService,
                          ActivityLogService activityLogService, ContactService contactService,
                          StudentService studentService) {
        this.dashboardService = dashboardService;
        this.membershipService = membershipService;
        this.activityLogService = activityLogService;
        this.contactService = contactService;
        this.studentService = studentService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("stats", dashboardService.getStats());
        model.addAttribute("membershipOpen", membershipService.isOpen());
        model.addAttribute("membershipPeriod", membershipService.getCurrentPeriod());
        model.addAttribute("recentActivities", activityLogService.recent());
        model.addAttribute("recentMessages", contactService.list(PageRequest.of(0, 5)).getContent());
        model.addAttribute("currentUser", Map.of("email", principal.getName()));
        return "admin/dashboard";
    }

    @GetMapping("/admin/students")
    public String students(Model model) {
        List<StudentResponse> students = studentService.findAll(PageRequest.of(0, 500)).getContent();
        List<String> departments = students.stream()
                .map(StudentResponse::department).distinct().sorted().toList();
        model.addAttribute("students", students);
        model.addAttribute("departments", departments);
        return "admin/students";
    }

    @GetMapping("/admin/students/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getById(id));
        return "admin/student-view";
    }

    @PostMapping("/admin/students/{id}/activate")
    public String activateStudent(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes ra) {
        studentService.setActive(id, active);
        ra.addFlashAttribute("successMessage", active ? "Student activated." : "Student deactivated.");
        return "redirect:/admin/students";
    }

    @PostMapping("/admin/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes ra) {
        studentService.delete(id);
        ra.addFlashAttribute("successMessage", "Student deleted.");
        return "redirect:/admin/students";
    }

    @PostMapping("/admin/students/bulk-delete")
    public String bulkDeleteStudents(@RequestParam(required = false) java.util.List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(studentService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected students deleted.");
        return "redirect:/admin/students";
    }
}
