package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.SocialLinkRequest;
import bd.edu.seu.gamesclub.dto.SocialLinkResponse;
import bd.edu.seu.gamesclub.entity.SocialLink;
import bd.edu.seu.gamesclub.exception.DuplicateResourceException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.SocialLinkMapper;
import bd.edu.seu.gamesclub.repository.SocialLinkRepository;
import bd.edu.seu.gamesclub.service.SocialLinkService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link SocialLinkService}. */
@Service
@Transactional(readOnly = true)
public class SocialLinkServiceImpl implements SocialLinkService {

    private final SocialLinkRepository socialLinkRepository;

    public SocialLinkServiceImpl(SocialLinkRepository socialLinkRepository) {
        this.socialLinkRepository = socialLinkRepository;
    }

    @Override
    public List<SocialLinkResponse> getActive() {
        return socialLinkRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(SocialLinkMapper::toResponse).toList();
    }

    @Override
    public List<SocialLinkResponse> getAll() {
        return socialLinkRepository.findAll().stream().map(SocialLinkMapper::toResponse).toList();
    }

    @Override
    public SocialLinkResponse getById(Long id) {
        return SocialLinkMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public SocialLinkResponse create(SocialLinkRequest request) {
        if (socialLinkRepository.existsByPlatform(request.platform())) {
            throw new DuplicateResourceException("A link for this platform already exists.");
        }
        SocialLink link = new SocialLink();
        SocialLinkMapper.apply(link, request);
        return SocialLinkMapper.toResponse(socialLinkRepository.save(link));
    }

    @Override
    @Transactional
    public SocialLinkResponse update(Long id, SocialLinkRequest request) {
        SocialLink link = findEntity(id);
        if (!link.getPlatform().equalsIgnoreCase(request.platform())
                && socialLinkRepository.existsByPlatform(request.platform())) {
            throw new DuplicateResourceException("A link for this platform already exists.");
        }
        SocialLinkMapper.apply(link, request);
        return SocialLinkMapper.toResponse(socialLinkRepository.save(link));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        socialLinkRepository.delete(findEntity(id));
    }

    private SocialLink findEntity(Long id) {
        return socialLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Social link", "id", id));
    }
}
