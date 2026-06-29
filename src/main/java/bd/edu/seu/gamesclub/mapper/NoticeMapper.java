package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.NoticeRequest;
import bd.edu.seu.gamesclub.dto.NoticeResponse;
import bd.edu.seu.gamesclub.entity.Notice;
import bd.edu.seu.gamesclub.entity.enums.NoticeType;

/** Manual mapper for {@link Notice}. */
public final class NoticeMapper {

    private NoticeMapper() {
    }

    public static NoticeResponse toResponse(Notice n) {
        if (n == null) {
            return null;
        }
        return new NoticeResponse(
                n.getId(),
                n.getTitle(),
                n.getContent(),
                n.getNoticeType() != null ? n.getNoticeType().name() : null,
                n.getPublishDate(),
                n.getExpiryDate(),
                n.isPinned(),
                n.isPublished()
        );
    }

    public static void apply(Notice n, NoticeRequest r) {
        n.setTitle(r.title());
        n.setContent(r.content());
        n.setNoticeType(NoticeType.valueOf(r.noticeType()));
        n.setPublishDate(r.publishDate());
        n.setExpiryDate(r.expiryDate());
        if (r.pinned() != null) {
            n.setPinned(r.pinned());
        }
        if (r.published() != null) {
            n.setPublished(r.published());
        }
    }
}
