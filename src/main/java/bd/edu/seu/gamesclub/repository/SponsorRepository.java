package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.Sponsor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Sponsor}s.
 */
@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {

    /** Active sponsors ordered by display order. */
    List<Sponsor> findByActiveTrueOrderByDisplayOrderAsc();
}
