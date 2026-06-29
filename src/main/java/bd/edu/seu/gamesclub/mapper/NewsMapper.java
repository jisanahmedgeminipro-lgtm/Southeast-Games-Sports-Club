package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.NewsRequest;
import bd.edu.seu.gamesclub.dto.NewsResponse;
import bd.edu.seu.gamesclub.entity.News;
import bd.edu.seu.gamesclub.entity.enums.PublishStatus;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link News}. Image relation is resolved by the service. */
public final class NewsMapper {

    private NewsMapper() {
    }

    public static NewsResponse toResponse(News n) {
        if (n == null) {
            return null;
        }
        return new NewsResponse(
                n.getId(),
                n.getTitle(),
                n.getSlug(),
                n.getContent(),
                MediaUrls.url(n.getImage()),
                n.getPublishDate(),
                n.getStatus() != null ? n.getStatus().name() : null,
                n.getImage() != null ? n.getImage().getId() : null
        );
    }

    public static void apply(News n, NewsRequest r) {
        n.setTitle(r.title());
        n.setContent(r.content());
        n.setPublishDate(r.publishDate());
        if (r.status() != null && !r.status().isBlank()) {
            n.setStatus(PublishStatus.valueOf(r.status()));
        }
    }
}
