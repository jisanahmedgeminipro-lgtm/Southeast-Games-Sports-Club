package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.GalleryImage;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link GalleryImage}s. Total gallery images is the inherited
 * {@link JpaRepository#count()}.
 */
@Repository
public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {

    /** Paginated images within a category. */
    Page<GalleryImage> findByCategoryId(Long categoryId, Pageable pageable);

    /** All images in a category, ordered by display order. */
    List<GalleryImage> findByCategoryIdOrderByDisplayOrderAsc(Long categoryId);
}
