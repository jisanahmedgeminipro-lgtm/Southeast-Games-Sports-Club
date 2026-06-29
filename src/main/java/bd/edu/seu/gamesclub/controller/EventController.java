package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.service.EventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Public events listing (paginated). */
@Controller
public class EventController {

    private static final int PAGE_SIZE = 9;

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public String events(@RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("eventDate").descending());
        model.addAttribute("eventsPage", eventService.getPage(null, pageable));
        return "events";
    }
}
