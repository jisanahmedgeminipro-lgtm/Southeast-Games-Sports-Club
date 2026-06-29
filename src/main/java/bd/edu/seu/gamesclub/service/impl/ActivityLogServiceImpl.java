package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.ActivityLogResponse;
import bd.edu.seu.gamesclub.entity.ActivityLog;
import bd.edu.seu.gamesclub.mapper.ActivityLogMapper;
import bd.edu.seu.gamesclub.repository.ActivityLogRepository;
import bd.edu.seu.gamesclub.service.ActivityLogService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link ActivityLogService}.
 */
@Service
@Transactional(readOnly = true)
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    @Transactional
    public void log(String action, String entityType, Long entityId, String description) {
        ActivityLog entry = new ActivityLog();
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDescription(description);
        activityLogRepository.save(entry);
    }

    @Override
    public List<ActivityLogResponse> recent() {
        return activityLogRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(ActivityLogMapper::toResponse).toList();
    }

    @Override
    public Page<ActivityLogResponse> findAll(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable).map(ActivityLogMapper::toResponse);
    }
}
