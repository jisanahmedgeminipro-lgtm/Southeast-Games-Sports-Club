package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.ActivityLog;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the admin {@link ActivityLog} audit trail.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /** The ten most recent activities (dashboard "Recent Activities"). */
    List<ActivityLog> findTop10ByOrderByCreatedAtDesc();

    /** Paginated full audit trail, newest first. */
    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
