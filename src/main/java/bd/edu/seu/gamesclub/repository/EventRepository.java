package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.Event;
import bd.edu.seu.gamesclub.entity.enums.EventStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Event}s. Total events is the inherited
 * {@link JpaRepository#count()}.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /** Find an event by its unique slug. */
    Optional<Event> findBySlug(String slug);

    /** Whether the slug is already taken. */
    boolean existsBySlug(String slug);

    /** Paginated search by (partial, case-insensitive) title. */
    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /** Paginated listing filtered by status. */
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    /** Upcoming events ordered by date (landing page). */
    List<Event> findByStatusOrderByEventDateAsc(EventStatus status);

    /** Events on/after a given date, ordered by date (alternative upcoming view). */
    List<Event> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate date);
}
