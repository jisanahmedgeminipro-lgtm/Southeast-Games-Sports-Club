package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.SportRequest;
import bd.edu.seu.gamesclub.dto.SportResponse;
import bd.edu.seu.gamesclub.entity.Sport;
import bd.edu.seu.gamesclub.exception.DuplicateResourceException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.SportMapper;
import bd.edu.seu.gamesclub.repository.SportRepository;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.SportService;
import bd.edu.seu.gamesclub.util.SlugUtil;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link SportService}. */
@Service
@Transactional(readOnly = true)
public class SportServiceImpl implements SportService {

    private final SportRepository sportRepository;
    private final MediaService mediaService;

    public SportServiceImpl(SportRepository sportRepository, MediaService mediaService) {
        this.sportRepository = sportRepository;
        this.mediaService = mediaService;
    }

    @Override
    public List<SportResponse> getActive() {
        return sportRepository.findByActiveTrueOrderByDisplayOrderAsc().stream().map(SportMapper::toResponse).toList();
    }

    @Override
    public List<SportResponse> getAll() {
        return sportRepository.findAllByOrderByDisplayOrderAsc().stream().map(SportMapper::toResponse).toList();
    }

    @Override
    public SportResponse getById(Long id) {
        return SportMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public SportResponse create(SportRequest request) {
        if (sportRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("A sport with this name already exists.");
        }
        Sport sport = new Sport();
        SportMapper.apply(sport, request);
        sport.setSlug(SlugUtil.uniqueSlug(request.name(), sportRepository::existsBySlug));
        sport.setIcon(mediaService.getReference(request.iconMediaId()));
        sport.setImage(mediaService.getReference(request.imageMediaId()));
        return SportMapper.toResponse(sportRepository.save(sport));
    }

    @Override
    @Transactional
    public SportResponse update(Long id, SportRequest request) {
        Sport sport = findEntity(id);
        if (!sport.getName().equalsIgnoreCase(request.name()) && sportRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("A sport with this name already exists.");
        }
        SportMapper.apply(sport, request);
        sport.setSlug(SlugUtil.uniqueSlug(request.name(),
                s -> sportRepository.existsBySlug(s) && !s.equals(sport.getSlug())));
        sport.setIcon(mediaService.getReference(request.iconMediaId()));
        sport.setImage(mediaService.getReference(request.imageMediaId()));
        return SportMapper.toResponse(sportRepository.save(sport));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sportRepository.delete(findEntity(id));
    }

    private Sport findEntity(Long id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sport", "id", id));
    }
}
