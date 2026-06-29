package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.News;
import bd.edu.seu.gamesclub.entity.enums.PublishStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link News} articles. Total news is the inherited
 * {@link JpaRepository#count()}.
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /** Find an article by its unique slug. */
    Optional<News> findBySlug(String slug);

    /** Whether the slug is already taken. */
    boolean existsBySlug(String slug);

    /** Paginated listing filtered by publication status (e.g. PUBLISHED). */
    Page<News> findByStatus(PublishStatus status, Pageable pageable);

    /** Paginated search by (partial, case-insensitive) title. */
    Page<News> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /** Published articles ordered by publish date descending (landing page). */
    List<News> findByStatusOrderByPublishDateDesc(PublishStatus status);
}
