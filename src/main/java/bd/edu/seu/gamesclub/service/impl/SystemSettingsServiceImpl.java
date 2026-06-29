package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.SystemSettingsRequest;
import bd.edu.seu.gamesclub.dto.SystemSettingsResponse;
import bd.edu.seu.gamesclub.entity.SystemSettings;
import bd.edu.seu.gamesclub.mapper.SettingsMapper;
import bd.edu.seu.gamesclub.repository.SystemSettingsRepository;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.SystemSettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link SystemSettingsService}. The settings live in a single row (id 1). */
@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {

    private static final Long SINGLETON_ID = 1L;

    private final SystemSettingsRepository systemSettingsRepository;
    private final MediaService mediaService;

    public SystemSettingsServiceImpl(SystemSettingsRepository systemSettingsRepository, MediaService mediaService) {
        this.systemSettingsRepository = systemSettingsRepository;
        this.mediaService = mediaService;
    }

    @Override
    @Transactional
    public SystemSettingsResponse get() {
        return SettingsMapper.toResponse(loadOrCreate());
    }

    @Override
    @Transactional
    public SystemSettingsResponse update(SystemSettingsRequest request) {
        SystemSettings settings = loadOrCreate();
        SettingsMapper.apply(settings, request);
        settings.setFavicon(mediaService.getReference(request.faviconMediaId()));
        return SettingsMapper.toResponse(systemSettingsRepository.save(settings));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMaintenanceMode() {
        return systemSettingsRepository.findById(SINGLETON_ID)
                .map(SystemSettings::isMaintenanceMode)
                .orElse(false);
    }

    private SystemSettings loadOrCreate() {
        return systemSettingsRepository.findById(SINGLETON_ID).orElseGet(() -> {
            SystemSettings settings = new SystemSettings();
            settings.setId(SINGLETON_ID);
            settings.setSiteTitle("SEU Games & Sports Club");
            settings.setThemeColor("#0f766e");
            return systemSettingsRepository.save(settings);
        });
    }
}
