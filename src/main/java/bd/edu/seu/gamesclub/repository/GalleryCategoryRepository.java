package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.GalleryCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for gallery albums / {@link GalleryCategory}s.
 */
@Repository
public interface GalleryCategoryRepository extends JpaRepository<GalleryCategory, Long> {

    /** Find a category by its unique slug. */
    Optional<GalleryCategory> findBySlug(String slug);

    /** Whether a category with the given name exists. */
    boolean existsByName(String name);

    /** All categories ordered alphabetically. */
    List<GalleryCategory> findAllByOrderByNameAsc();
}
