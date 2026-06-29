package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.ActivityLogResponse;
import bd.edu.seu.gamesclub.entity.ActivityLog;

/** Manual mapper for {@link ActivityLog}. */
public final class ActivityLogMapper {

    private ActivityLogMapper() {
    }

    public static ActivityLogResponse toResponse(ActivityLog a) {
        if (a == null) {
            return null;
        }
        return new ActivityLogResponse(
                a.getId(), a.getAction(), a.getEntityType(), a.getEntityId(),
                a.getDescription(), a.getCreatedBy(), a.getCreatedAt()
        );
    }
}
