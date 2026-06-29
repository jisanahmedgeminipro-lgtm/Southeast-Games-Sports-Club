package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.TrainingSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link TrainingSchedule} practice slots.
 *
 * <p>Ordered by start time; weekday grouping (Mon..Sun) is applied in the
 * service layer, because {@code day_of_week} is stored as a string and would
 * otherwise sort alphabetically rather than chronologically.
 */
@Repository
public interface TrainingScheduleRepository extends JpaRepository<TrainingSchedule, Long> {

    /** Active schedules ordered by start time. */
    List<TrainingSchedule> findByActiveTrueOrderByStartTimeAsc();

    /** Active schedules for a sport, ordered by start time. */
    List<TrainingSchedule> findBySportIdAndActiveTrueOrderByStartTimeAsc(Long sportId);
}
