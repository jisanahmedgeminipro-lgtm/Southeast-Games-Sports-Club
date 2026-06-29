package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.ActivityLogResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Records and retrieves the audit trail of important admin actions.
 */
public interface ActivityLogService {

    /**
     * Record an admin action.
     *
     * @param action      machine-readable code (e.g. {@code MEMBERSHIP_OPENED})
     * @param entityType  affected entity type name (nullable)
     * @param entityId    affected entity id (nullable)
     * @param description human-readable summary
     */
    void log(String action, String entityType, Long entityId, String description);

    /** The most recent activities for the dashboard widget. */
    List<ActivityLogResponse> recent();

    /** Paginated full audit trail. */
    Page<ActivityLogResponse> findAll(Pageable pageable);
}
