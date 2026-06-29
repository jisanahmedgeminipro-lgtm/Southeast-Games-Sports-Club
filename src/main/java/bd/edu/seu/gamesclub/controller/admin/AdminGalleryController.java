package bd.edu.seu.gamesclub.controller.admin;

import bd.edu.seu.gamesclub.dto.GalleryCategoryRequest;
import bd.edu.seu.gamesclub.dto.GalleryCategoryResponse;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.service.GalleryService;
import bd.edu.seu.gamesclub.service.MediaService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin management of gallery categories and their images. */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminGalleryController {

    private final GalleryService galleryService;
    private final MediaService mediaService;

    public AdminGalleryController(GalleryService galleryService, MediaService mediaService) {
        this.galleryService = galleryService;
        this.mediaService = mediaService;
    }

    /* ----------------------------- Categories ----------------------------- */
    @GetMapping("/admin/gallery")
    public String categories(Model model) {
        model.addAttribute("categories", galleryService.getCategories());
        return "admin/gallery/categories";
    }

    @GetMapping("/admin/gallery/categories/new")
    public String createCategoryForm(Model model) {
        model.addAttribute("category", null);
        return "admin/gallery/category-form";
    }

    @GetMapping("/admin/gallery/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", findCategory(id));
        return "admin/gallery/category-form";
    }

    @PostMapping("/admin/gallery/categories/save")
    public String saveCategory(@RequestParam(required = false) Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              RedirectAttributes ra) {
        GalleryCategoryRequest req = new GalleryCategoryRequest(name, description);
        if (id == null) {
            galleryService.createCategory(req);
            ra.addFlashAttribute("successMessage", "Category created.");
        } else {
            galleryService.updateCategory(id, req);
            ra.addFlashAttribute("successMessage", "Category updated.");
        }
        return "redirect:/admin/gallery";
    }

    @PostMapping("/admin/gallery/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        galleryService.deleteCategory(id);
        ra.addFlashAttribute("successMessage", "Category and its images deleted.");
        return "redirect:/admin/gallery";
    }

    /* ----------------------------- Images ----------------------------- */
    @GetMapping("/admin/gallery/categories/{id}/images")
    public String images(@PathVariable Long id, Model model) {
        model.addAttribute("category", findCategory(id));
        model.addAttribute("images", galleryService.getImagesByCategory(id));
        return "admin/gallery/images";
    }

    @PostMapping("/admin/gallery/images/upload")
    public String uploadImage(@RequestParam Long categoryId,
                             @RequestParam(required = false) String caption,
                             @RequestParam(required = false) Integer displayOrder,
                             @RequestParam MultipartFile imageFile,
                             RedirectAttributes ra) {
        if (imageFile == null || imageFile.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Please choose an image to upload.");
            return "redirect:/admin/gallery/categories/" + categoryId + "/images";
        }
        Long mediaId = mediaService.store(imageFile, caption).id();
        galleryService.addImage(categoryId, mediaId, caption, displayOrder);
        ra.addFlashAttribute("successMessage", "Image uploaded.");
        return "redirect:/admin/gallery/categories/" + categoryId + "/images";
    }

    @PostMapping("/admin/gallery/images/{id}/delete")
    public String deleteImage(@PathVariable Long id, @RequestParam Long categoryId, RedirectAttributes ra) {
        galleryService.deleteImage(id);
        ra.addFlashAttribute("successMessage", "Image deleted.");
        return "redirect:/admin/gallery/categories/" + categoryId + "/images";
    }

    @PostMapping("/admin/gallery/categories/bulk-delete")
    public String bulkDeleteCategories(@RequestParam(required = false) List<Long> ids, RedirectAttributes ra) {
        if (ids != null) {
            ids.forEach(galleryService::deleteCategory);
        }
        ra.addFlashAttribute("successMessage", "Selected categories deleted.");
        return "redirect:/admin/gallery";
    }

    private GalleryCategoryResponse findCategory(Long id) {
        return galleryService.getCategories().stream()
                .filter(c -> c.id().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Gallery category", "id", id));
    }
}
