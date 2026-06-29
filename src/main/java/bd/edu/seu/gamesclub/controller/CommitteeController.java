package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.entity.enums.CommitteeType;
import bd.edu.seu.gamesclub.service.CommitteeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Public Executive and Sub-Executive committee pages. */
@Controller
public class CommitteeController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService) {
        this.committeeService = committeeService;
    }

    @GetMapping("/committee/executive")
    public String executive(Model model) {
        model.addAttribute("committeeMembers", committeeService.getActiveByType(CommitteeType.EXECUTIVE));
        return "committee/executive";
    }

    @GetMapping("/committee/sub-executive")
    public String subExecutive(Model model) {
        model.addAttribute("committeeMembers", committeeService.getActiveByType(CommitteeType.SUB_EXECUTIVE));
        return "committee/sub-executive";
    }
}
