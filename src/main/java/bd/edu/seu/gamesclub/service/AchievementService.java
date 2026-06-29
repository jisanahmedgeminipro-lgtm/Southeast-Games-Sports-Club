package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.AchievementRequest;
import bd.edu.seu.gamesclub.dto.AchievementResponse;
import java.util.List;

/** Manages achievements. */
public interface AchievementService {

    /** All achievements, most recent year first. */
    List<AchievementResponse> getAll();

    AchievementResponse getById(Long id);

    AchievementResponse create(AchievementRequest request);

    AchievementResponse update(Long id, AchievementRequest request);

    void delete(Long id);
}
