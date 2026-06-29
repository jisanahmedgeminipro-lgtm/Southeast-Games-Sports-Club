package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.ClubSettingsRequest;
import bd.edu.seu.gamesclub.dto.ClubSettingsResponse;
import bd.edu.seu.gamesclub.entity.ClubSettings;
import bd.edu.seu.gamesclub.mapper.SettingsMapper;
import bd.edu.seu.gamesclub.repository.ClubSettingsRepository;
import bd.edu.seu.gamesclub.service.ClubSettingsService;
import bd.edu.seu.gamesclub.service.MediaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link ClubSettingsService}. The settings live in a single row (id 1). */
@Service
public class ClubSettingsServiceImpl implements ClubSettingsService {

    private static final Long SINGLETON_ID = 1L;

    private final ClubSettingsRepository clubSettingsRepository;
    private final MediaService mediaService;

    public ClubSettingsServiceImpl(ClubSettingsRepository clubSettingsRepository, MediaService mediaService) {
        this.clubSettingsRepository = clubSettingsRepository;
        this.mediaService = mediaService;
    }

    @Override
    @Transactional
    public ClubSettingsResponse get() {
        return SettingsMapper.toResponse(loadOrCreate());
    }

    @Override
    @Transactional
    public ClubSettingsResponse update(ClubSettingsRequest request) {
        ClubSettings settings = loadOrCreate();
        SettingsMapper.apply(settings, request);
        settings.setLogo(mediaService.getReference(request.logoMediaId()));
        return SettingsMapper.toResponse(clubSettingsRepository.save(settings));
    }

    private ClubSettings loadOrCreate() {
        return clubSettingsRepository.findById(SINGLETON_ID).orElseGet(() -> {
            ClubSettings settings = new ClubSettings();
            settings.setId(SINGLETON_ID);
            settings.setUniversityName("Southeast University");
            settings.setClubName("Games & Sports Club");
            return clubSettingsRepository.save(settings);
        });
    }
}
