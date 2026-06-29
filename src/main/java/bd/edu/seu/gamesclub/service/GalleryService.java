package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.GalleryCategoryRequest;
import bd.edu.seu.gamesclub.dto.GalleryCategoryResponse;
import bd.edu.seu.gamesclub.dto.GalleryImageResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Manages gallery categories and their images. */
public interface GalleryService {

    /** All categories (public filters + admin). */
    List<GalleryCategoryResponse> getCategories();

    GalleryCategoryResponse createCategory(GalleryCategoryRequest request);

    GalleryCategoryResponse updateCategory(Long id, GalleryCategoryRequest request);

    void deleteCategory(Long id);

    /** Paginated images across all categories (public gallery page). */
    Page<GalleryImageResponse> getImagesPage(Pageable pageable);

    /** All images within a category, ordered by display order. */
    List<GalleryImageResponse> getImagesByCategory(Long categoryId);

    /** Add an uploaded image (referenced by media id) to a category. */
    GalleryImageResponse addImage(Long categoryId, Long mediaId, String caption, Integer displayOrder);

    void deleteImage(Long imageId);
}
