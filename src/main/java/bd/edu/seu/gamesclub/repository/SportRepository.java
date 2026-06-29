package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.Sport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Sport}s.
 */
@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {

    /** Find a sport by its unique slug. */
    Optional<Sport> findBySlug(String slug);

    /** Whether a sport with the given name exists (admin add guard). */
    boolean existsByName(String name);

    /** Whether a sport with the given slug exists. */
    boolean existsBySlug(String slug);

    /** Public view: active sports ordered by display order. */
    List<Sport> findByActiveTrueOrderByDisplayOrderAsc();

    /** Admin view: all sports ordered by display order. */
    List<Sport> findAllByOrderByDisplayOrderAsc();
}
