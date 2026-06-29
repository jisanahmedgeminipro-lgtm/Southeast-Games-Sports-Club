package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.Achievement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Achievement}s.
 */
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    /** Achievements ordered by most recent year, then display order. */
    List<Achievement> findAllByOrderByAchievementYearDescDisplayOrderAsc();
}
