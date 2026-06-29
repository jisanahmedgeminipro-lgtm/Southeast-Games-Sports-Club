package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.dto.GalleryImageResponse;
import bd.edu.seu.gamesclub.service.GalleryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Public gallery (category filters + paginated images). */
@Controller
public class GalleryController {

    private static final int PAGE_SIZE = 12;

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @GetMapping("/gallery")
    public String gallery(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<GalleryImageResponse> images = galleryService.getImagesPage(PageRequest.of(Math.max(page, 0), PAGE_SIZE));
        model.addAttribute("galleryCategories", galleryService.getCategories());
        model.addAttribute("galleryImages", images.getContent());
        model.addAttribute("galleryPage", images);
        return "gallery";
    }
}
