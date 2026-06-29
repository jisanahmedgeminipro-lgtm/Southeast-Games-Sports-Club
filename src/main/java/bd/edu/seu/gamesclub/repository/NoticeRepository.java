package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.Notice;
import bd.edu.seu.gamesclub.entity.enums.NoticeType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for notice-board {@link Notice}s.
 */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /** Paginated published notices: pinned first, then newest publish date. */
    Page<Notice> findByPublishedTrueOrderByPinnedDescPublishDateDesc(Pageable pageable);

    /** Published notices of a given type, newest first. */
    List<Notice> findByNoticeTypeAndPublishedTrueOrderByPublishDateDesc(NoticeType noticeType);
}
