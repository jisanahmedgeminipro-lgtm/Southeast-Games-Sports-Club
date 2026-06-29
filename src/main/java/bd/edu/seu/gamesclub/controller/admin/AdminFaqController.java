package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.FaqRequest;
import bd.edu.seu.gamesclub.service.FaqService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin CRUD for FAQs. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminFaqController {

    private final FaqService faqService;

    public AdminFaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/admin/faqs")
    public String list(Model model) {
        model.addAttribute("faqs", faqService.getAll());
        return "admin/faqs/list";
    }

    @GetMapping("/admin/faqs/new")
    public String createForm(Model model) {
        model.addAttribute("faq", null);
        return "admin/faqs/form";
    }

    @GetMapping("/admin/faqs/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("faq", faqService.getById(id));
        return "admin/faqs/form";
    }

    @PostMapping("/admin/faqs/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String question,
                      @RequestParam String answer,
                      @RequestParam(required = false) Integer displayOrder,
                      @RequestParam(defaultValue = "false") boolean active,
                      RedirectAttributes ra) {
        FaqRequest req = new FaqRequest(question, answer, displayOrder, active);
        if (id == null) {
            faqService.create(req);
            ra.addFlashAttribute("successMessage", "FAQ created.");
        } else {
            faqService.update(id, req);
            ra.addFlashAttribute("successMessage", "FAQ updated.");
        }
        return "redirect:/admin/faqs";
    }

    @PostMapping("/admin/faqs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        faqService.delete(id);
        ra.addFlashAttribute("successMessage", "FAQ deleted.");
        return "redirect:/admin/faqs";
    }

    @PostMapping("/admin/faqs/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(faqService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected FAQs deleted.");
        return "redirect:/admin/faqs";
    }
}
