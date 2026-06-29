package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.HeroSlider;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for landing-page {@link HeroSlider} slides.
 */
@Repository
public interface HeroSliderRepository extends JpaRepository<HeroSlider, Long> {

    /** Active slides ordered by display order (the public hero carousel). */
    List<HeroSlider> findByActiveTrueOrderByDisplayOrderAsc();
}
