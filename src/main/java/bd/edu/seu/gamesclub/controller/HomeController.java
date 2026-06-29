package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.entity.enums.CommitteeType;
import bd.edu.seu.gamesclub.service.AchievementService;
import bd.edu.seu.gamesclub.service.CommitteeService;
import bd.edu.seu.gamesclub.service.EventService;
import bd.edu.seu.gamesclub.service.FaqService;
import bd.edu.seu.gamesclub.service.GalleryService;
import bd.edu.seu.gamesclub.service.HeroSliderService;
import bd.edu.seu.gamesclub.service.MembershipService;
import bd.edu.seu.gamesclub.service.NewsService;
import bd.edu.seu.gamesclub.service.SportService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the public marketing pages (landing, about, sports, achievements,
 * membership). Shared navbar/footer data is supplied by {@code GlobalModelAdvice}.
 */
@Controller
public class HomeController {

    private final HeroSliderService heroSliderService;
    private final SportService sportService;
    private final CommitteeService committeeService;
    private final EventService eventService;
    private final NewsService newsService;
    private final AchievementService achievementService;
    private final GalleryService galleryService;
    private final MembershipService membershipService;
    private final FaqService faqService;

    public HomeController(HeroSliderService heroSliderService, SportService sportService,
                          CommitteeService committeeService, EventService eventService,
                          NewsService newsService, AchievementService achievementService,
                          GalleryService galleryService, MembershipService membershipService,
                          FaqService faqService) {
        this.heroSliderService = heroSliderService;
        this.sportService = sportService;
        this.committeeService = committeeService;
        this.eventService = eventService;
        this.newsService = newsService;
        this.achievementService = achievementService;
        this.galleryService = galleryService;
        this.membershipService = membershipService;
        this.faqService = faqService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("heroSliders", heroSliderService.getActive());
        model.addAttribute("sportsList", sportService.getActive());
        model.addAttribute("executiveCommittee", committeeService.getActiveByType(CommitteeType.EXECUTIVE));
        model.addAttribute("upcomingEvents", eventService.getUpcoming());
        model.addAttribute("latestNews", newsService.getPublished());
        model.addAttribute("achievements", achievementService.getAll());
        model.addAttribute("galleryImages", galleryService.getImagesPage(PageRequest.of(0, 9)).getContent());
        model.addAttribute("membershipOpen", membershipService.isOpen());
        model.addAttribute("membershipPeriod", membershipService.getCurrentPeriod());
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/sports")
    public String sports(Model model) {
        model.addAttribute("sportsList", sportService.getActive());
        return "sports";
    }

    @GetMapping("/achievements")
    public String achievements(Model model) {
        model.addAttribute("achievements", achievementService.getAll());
        return "achievements";
    }

    @GetMapping("/membership")
    public String membership(Model model) {
        model.addAttribute("membershipOpen", membershipService.isOpen());
        model.addAttribute("membershipPeriod", membershipService.getCurrentPeriod());
        model.addAttribute("faqs", faqService.getActive());
        return "membership";
    }
}
