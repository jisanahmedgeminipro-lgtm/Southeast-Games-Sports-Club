package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.service.ContactService;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin view/manage contact messages. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminContactController {

    private final ContactService contactService;

    public AdminContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/admin/messages")
    public String list(Model model) {
        model.addAttribute("messages", contactService.list(PageRequest.of(0, 500)).getContent());
        return "admin/messages/list";
    }

    @GetMapping("/admin/messages/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("message", contactService.getById(id));
        contactService.markRead(id);
        return "admin/messages/view";
    }

    @PostMapping("/admin/messages/{id}/read")
    public String markRead(@PathVariable Long id, RedirectAttributes ra) {
        contactService.markRead(id);
        ra.addFlashAttribute("successMessage", "Message marked as read.");
        return "redirect:/admin/messages";
    }

    @PostMapping("/admin/messages/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        contactService.delete(id);
        ra.addFlashAttribute("successMessage", "Message deleted.");
        return "redirect:/admin/messages";
    }

    @PostMapping("/admin/messages/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(contactService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected messages deleted.");
        return "redirect:/admin/messages";
    }
}
