package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.service.NewsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Public news listing (paginated, published only). */
@Controller
public class NewsController {

    private static final int PAGE_SIZE = 9;

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public String news(@RequestParam(defaultValue = "0") int page, Model model) {
        var pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("publishDate").descending());
        model.addAttribute("newsPage", newsService.getPublishedPage(pageable));
        return "news";
    }
}
