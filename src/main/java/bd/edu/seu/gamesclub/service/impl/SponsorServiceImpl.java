package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.SponsorRequest;
import bd.edu.seu.gamesclub.dto.SponsorResponse;
import bd.edu.seu.gamesclub.entity.Sponsor;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.SponsorMapper;
import bd.edu.seu.gamesclub.repository.SponsorRepository;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.SponsorService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link SponsorService}. */
@Service
@Transactional(readOnly = true)
public class SponsorServiceImpl implements SponsorService {

    private final SponsorRepository sponsorRepository;
    private final MediaService mediaService;

    public SponsorServiceImpl(SponsorRepository sponsorRepository, MediaService mediaService) {
        this.sponsorRepository = sponsorRepository;
        this.mediaService = mediaService;
    }

    @Override
    public List<SponsorResponse> getActive() {
        return sponsorRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(SponsorMapper::toResponse).toList();
    }

    @Override
    public List<SponsorResponse> getAll() {
        return sponsorRepository.findAll().stream().map(SponsorMapper::toResponse).toList();
    }

    @Override
    public SponsorResponse getById(Long id) {
        return SponsorMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public SponsorResponse create(SponsorRequest request) {
        Sponsor sponsor = new Sponsor();
        SponsorMapper.apply(sponsor, request);
        sponsor.setLogo(mediaService.getReference(request.logoMediaId()));
        return SponsorMapper.toResponse(sponsorRepository.save(sponsor));
    }

    @Override
    @Transactional
    public SponsorResponse update(Long id, SponsorRequest request) {
        Sponsor sponsor = findEntity(id);
        SponsorMapper.apply(sponsor, request);
        sponsor.setLogo(mediaService.getReference(request.logoMediaId()));
        return SponsorMapper.toResponse(sponsorRepository.save(sponsor));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sponsorRepository.delete(findEntity(id));
    }

    private Sponsor findEntity(Long id) {
        return sponsorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsor", "id", id));
    }
}
