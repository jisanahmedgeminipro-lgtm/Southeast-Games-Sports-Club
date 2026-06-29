package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.AchievementRequest;
import bd.edu.seu.gamesclub.dto.AchievementResponse;
import bd.edu.seu.gamesclub.entity.Achievement;
import bd.edu.seu.gamesclub.entity.Sport;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.AchievementMapper;
import bd.edu.seu.gamesclub.repository.AchievementRepository;
import bd.edu.seu.gamesclub.repository.SportRepository;
import bd.edu.seu.gamesclub.service.AchievementService;
import bd.edu.seu.gamesclub.service.MediaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link AchievementService}. */
@Service
@Transactional(readOnly = true)
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final SportRepository sportRepository;
    private final MediaService mediaService;

    public AchievementServiceImpl(AchievementRepository achievementRepository,
                                  SportRepository sportRepository,
                                  MediaService mediaService) {
        this.achievementRepository = achievementRepository;
        this.sportRepository = sportRepository;
        this.mediaService = mediaService;
    }

    @Override
    public List<AchievementResponse> getAll() {
        return achievementRepository.findAllByOrderByAchievementYearDescDisplayOrderAsc().stream()
                .map(AchievementMapper::toResponse).toList();
    }

    @Override
    public AchievementResponse getById(Long id) {
        return AchievementMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public AchievementResponse create(AchievementRequest request) {
        Achievement achievement = new Achievement();
        AchievementMapper.apply(achievement, request);
        achievement.setImage(mediaService.getReference(request.imageMediaId()));
        achievement.setSport(resolveSport(request.sportId()));
        return AchievementMapper.toResponse(achievementRepository.save(achievement));
    }

    @Override
    @Transactional
    public AchievementResponse update(Long id, AchievementRequest request) {
        Achievement achievement = findEntity(id);
        AchievementMapper.apply(achievement, request);
        achievement.setImage(mediaService.getReference(request.imageMediaId()));
        achievement.setSport(resolveSport(request.sportId()));
        return AchievementMapper.toResponse(achievementRepository.save(achievement));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        achievementRepository.delete(findEntity(id));
    }

    private Sport resolveSport(Long sportId) {
        if (sportId == null) {
            return null;
        }
        return sportRepository.findById(sportId)
                .orElseThrow(() -> new ResourceNotFoundException("Sport", "id", sportId));
    }

    private Achievement findEntity(Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));
    }
}
