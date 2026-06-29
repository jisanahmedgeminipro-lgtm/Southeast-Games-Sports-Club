package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.NoticeRequest;
import bd.edu.seu.gamesclub.dto.NoticeResponse;
import bd.edu.seu.gamesclub.entity.Notice;
import bd.edu.seu.gamesclub.entity.enums.NoticeType;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.NoticeMapper;
import bd.edu.seu.gamesclub.repository.NoticeRepository;
import bd.edu.seu.gamesclub.service.NoticeService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link NoticeService}. */
@Service
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public Page<NoticeResponse> getPublished(Pageable pageable) {
        return noticeRepository.findByPublishedTrueOrderByPinnedDescPublishDateDesc(pageable)
                .map(NoticeMapper::toResponse);
    }

    @Override
    public List<NoticeResponse> getByType(NoticeType type) {
        return noticeRepository.findByNoticeTypeAndPublishedTrueOrderByPublishDateDesc(type)
                .stream().map(NoticeMapper::toResponse).toList();
    }

    @Override
    public NoticeResponse getById(Long id) {
        return NoticeMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public NoticeResponse create(NoticeRequest request) {
        Notice notice = new Notice();
        NoticeMapper.apply(notice, request);
        return NoticeMapper.toResponse(noticeRepository.save(notice));
    }

    @Override
    @Transactional
    public NoticeResponse update(Long id, NoticeRequest request) {
        Notice notice = findEntity(id);
        NoticeMapper.apply(notice, request);
        return NoticeMapper.toResponse(noticeRepository.save(notice));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        noticeRepository.delete(findEntity(id));
    }

    private Notice findEntity(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "id", id));
    }
}
