package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.dto.ContactRequest;
import bd.edu.seu.gamesclub.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Public contact page and form submission. */
@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @PostMapping("/contact")
    public String submit(@Valid @ModelAttribute ContactRequest form, BindingResult binding,
                         HttpServletRequest request, Model model, RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("errorMessage", "Please fill in all required fields correctly.");
            return "contact";
        }
        contactService.submit(form, request.getRemoteAddr());
        ra.addFlashAttribute("successMessage", "Thank you! Your message has been sent.");
        return "redirect:/contact";
    }
}
