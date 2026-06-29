package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.FaqRequest;
import bd.edu.seu.gamesclub.dto.FaqResponse;
import bd.edu.seu.gamesclub.entity.Faq;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.FaqMapper;
import bd.edu.seu.gamesclub.repository.FaqRepository;
import bd.edu.seu.gamesclub.service.FaqService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link FaqService}. */
@Service
@Transactional(readOnly = true)
public class FaqServiceImpl implements FaqService {

    private final FaqRepository faqRepository;

    public FaqServiceImpl(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public List<FaqResponse> getActive() {
        return faqRepository.findByActiveTrueOrderByDisplayOrderAsc().stream().map(FaqMapper::toResponse).toList();
    }

    @Override
    public List<FaqResponse> getAll() {
        return faqRepository.findAll().stream().map(FaqMapper::toResponse).toList();
    }

    @Override
    public FaqResponse getById(Long id) {
        return FaqMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public FaqResponse create(FaqRequest request) {
        Faq faq = new Faq();
        FaqMapper.apply(faq, request);
        return FaqMapper.toResponse(faqRepository.save(faq));
    }

    @Override
    @Transactional
    public FaqResponse update(Long id, FaqRequest request) {
        Faq faq = findEntity(id);
        FaqMapper.apply(faq, request);
        return FaqMapper.toResponse(faqRepository.save(faq));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        faqRepository.delete(findEntity(id));
    }

    private Faq findEntity(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", "id", id));
    }
}
