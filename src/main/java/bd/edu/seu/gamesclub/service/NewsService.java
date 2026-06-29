package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.NewsRequest;
import bd.edu.seu.gamesclub.dto.NewsResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Manages news articles. */
public interface NewsService {

    /** Published articles, newest first (public landing). */
    List<NewsResponse> getPublished();

    /** Paginated published articles. */
    Page<NewsResponse> getPublishedPage(Pageable pageable);

    /** Paginated title search (admin). */
    Page<NewsResponse> search(String title, Pageable pageable);

    NewsResponse getBySlug(String slug);

    NewsResponse getById(Long id);

    NewsResponse create(NewsRequest request);

    NewsResponse update(Long id, NewsRequest request);

    void delete(Long id);
}
