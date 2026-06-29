package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.HeroSliderRequest;
import bd.edu.seu.gamesclub.dto.HeroSliderResponse;
import bd.edu.seu.gamesclub.entity.HeroSlider;
import bd.edu.seu.gamesclub.exception.DuplicateResourceException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.HeroSliderMapper;
import bd.edu.seu.gamesclub.repository.HeroSliderRepository;
import bd.edu.seu.gamesclub.service.HeroSliderService;
import bd.edu.seu.gamesclub.service.MediaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link HeroSliderService}. */
@Service
@Transactional(readOnly = true)
public class HeroSliderServiceImpl implements HeroSliderService {

    private final HeroSliderRepository heroSliderRepository;
    private final MediaService mediaService;

    public HeroSliderServiceImpl(HeroSliderRepository heroSliderRepository, MediaService mediaService) {
        this.heroSliderRepository = heroSliderRepository;
        this.mediaService = mediaService;
    }

    @Override
    public List<HeroSliderResponse> getActive() {
        return heroSliderRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(HeroSliderMapper::toResponse).toList();
    }

    @Override
    public List<HeroSliderResponse> getAll() {
        return heroSliderRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                .map(HeroSliderMapper::toResponse).toList();
    }

    @Override
    public HeroSliderResponse getById(Long id) {
        return HeroSliderMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public HeroSliderResponse create(HeroSliderRequest request) {
        HeroSlider slider = new HeroSlider();
        HeroSliderMapper.apply(slider, request);
        slider.setDisplayOrder(resolveUniqueOrder(request.displayOrder(), null));
        slider.setBackground(mediaService.getReference(request.backgroundMediaId()));
        return HeroSliderMapper.toResponse(heroSliderRepository.save(slider));
    }

    @Override
    @Transactional
    public HeroSliderResponse update(Long id, HeroSliderRequest request) {
        HeroSlider slider = findEntity(id);
        HeroSliderMapper.apply(slider, request);
        slider.setDisplayOrder(resolveUniqueOrder(request.displayOrder(), slider.getId()));
        slider.setBackground(mediaService.getReference(request.backgroundMediaId()));
        return HeroSliderMapper.toResponse(heroSliderRepository.save(slider));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        heroSliderRepository.delete(findEntity(id));
    }

    /**
     * Returns a unique display order. When {@code requested} is null the next free
     * slot is assigned; otherwise it is validated against the other slides.
     */
    private int resolveUniqueOrder(Integer requested, Long selfId) {
        List<HeroSlider> all = heroSliderRepository.findAll();
        if (requested == null) {
            return all.stream().mapToInt(HeroSlider::getDisplayOrder).max().orElse(0) + 1;
        }
        boolean taken = all.stream().anyMatch(h ->
                h.getDisplayOrder() == requested && (selfId == null || !h.getId().equals(selfId)));
        if (taken) {
            throw new DuplicateResourceException("Display order " + requested + " is already in use.");
        }
        return requested;
    }

    private HeroSlider findEntity(Long id) {
        return heroSliderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hero slide", "id", id));
    }
}
