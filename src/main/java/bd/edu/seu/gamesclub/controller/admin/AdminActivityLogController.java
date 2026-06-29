package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.service.ActivityLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Read-only admin viewer for the activity audit trail. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminActivityLogController {

    private final ActivityLogService activityLogService;

    public AdminActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/admin/activity-logs")
    public String list(Model model) {
        model.addAttribute("logs", activityLogService.findAll(PageRequest.of(0, 1000)).getContent());
        return "admin/logs/list";
    }
}
