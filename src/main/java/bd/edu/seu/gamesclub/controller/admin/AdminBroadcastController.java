package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.BroadcastRequest;
import bd.edu.seu.gamesclub.exception.BusinessException;
import bd.edu.seu.gamesclub.service.EmailBroadcastService;
import bd.edu.seu.gamesclub.service.StudentService;
import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin email broadcast composer and history. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminBroadcastController {

    private final EmailBroadcastService broadcastService;
    private final StudentService studentService;

    public AdminBroadcastController(EmailBroadcastService broadcastService, StudentService studentService) {
        this.broadcastService = broadcastService;
        this.studentService = studentService;
    }

    @GetMapping("/admin/broadcast")
    public String compose(Model model) {
        model.addAttribute("students", studentService.findAll(PageRequest.of(0, 500)).getContent());
        model.addAttribute("history", broadcastService.history(PageRequest.of(0, 50)).getContent());
        return "admin/broadcast/compose";
    }

    @PostMapping("/admin/broadcast/send")
    public String send(@RequestParam String subject,
                      @RequestParam String body,
                      @RequestParam String targetType,
                      @RequestParam(required = false) List<Long> recipientUserIds,
                      Principal principal, RedirectAttributes ra) {
        try {
            broadcastService.send(new BroadcastRequest(subject, body, targetType, recipientUserIds), principal.getName());
            ra.addFlashAttribute("successMessage", "Broadcast sent.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/broadcast";
    }
}
