package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.SocialLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link SocialLink}s (single source of truth for social URLs).
 */
@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {

    /** Find a link by its platform key. */
    Optional<SocialLink> findByPlatform(String platform);

    /** Whether a link already exists for the platform (unique guard). */
    boolean existsByPlatform(String platform);

    /** Active links ordered by display order (header/footer icons). */
    List<SocialLink> findByActiveTrueOrderByDisplayOrderAsc();
}
