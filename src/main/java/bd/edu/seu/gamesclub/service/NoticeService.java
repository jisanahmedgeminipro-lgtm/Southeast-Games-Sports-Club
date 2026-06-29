package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.NoticeRequest;
import bd.edu.seu.gamesclub.dto.NoticeResponse;
import bd.edu.seu.gamesclub.entity.enums.NoticeType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Manages notice-board entries. */
public interface NoticeService {

    /** Paginated published notices: pinned first, then newest. */
    Page<NoticeResponse> getPublished(Pageable pageable);

    /** Published notices of a given type. */
    List<NoticeResponse> getByType(NoticeType type);

    NoticeResponse getById(Long id);

    NoticeResponse create(NoticeRequest request);

    NoticeResponse update(Long id, NoticeRequest request);

    void delete(Long id);
}
