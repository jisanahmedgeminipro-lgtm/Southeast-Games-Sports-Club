package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.NewsRequest;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.NewsService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin CRUD for news articles. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminNewsController {

    private final NewsService newsService;
    private final MediaService mediaService;

    public AdminNewsController(NewsService newsService, MediaService mediaService) {
        this.newsService = newsService;
        this.mediaService = mediaService;
    }

    @GetMapping("/admin/news")
    public String list(Model model) {
        model.addAttribute("newsList", newsService.search("", PageRequest.of(0, 500)).getContent());
        return "admin/news/list";
    }

    @GetMapping("/admin/news/new")
    public String createForm(Model model) {
        model.addAttribute("news", null);
        return "admin/news/form";
    }

    @GetMapping("/admin/news/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("news", newsService.getById(id));
        return "admin/news/form";
    }

    @PostMapping("/admin/news/save")
    public String save(@RequestParam(required = false) Long id,
                      @RequestParam String title,
                      @RequestParam String content,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publishDate,
                      @RequestParam(required = false) String status,
                      @RequestParam(required = false) Long currentImageMediaId,
                      @RequestParam(required = false) MultipartFile imageFile,
                      RedirectAttributes ra) {
        Long imageId = (imageFile != null && !imageFile.isEmpty())
                ? mediaService.store(imageFile, title).id() : currentImageMediaId;
        NewsRequest req = new NewsRequest(title, content, imageId, publishDate, status);
        if (id == null) {
            newsService.create(req);
            ra.addFlashAttribute("successMessage", "News article created.");
        } else {
            newsService.update(id, req);
            ra.addFlashAttribute("successMessage", "News article updated.");
        }
        return "redirect:/admin/news";
    }

    @PostMapping("/admin/news/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        newsService.delete(id);
        ra.addFlashAttribute("successMessage", "News article deleted.");
        return "redirect:/admin/news";
    }

    @PostMapping("/admin/news/bulk-delete")
    public String bulkDelete(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(newsService::delete);
        }
        ra.addFlashAttribute("successMessage", "Selected articles deleted.");
        return "redirect:/admin/news";
    }
}
