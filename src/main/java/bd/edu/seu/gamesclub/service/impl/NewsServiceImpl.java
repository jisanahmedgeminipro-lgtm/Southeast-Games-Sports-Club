package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.NewsRequest;
import bd.edu.seu.gamesclub.dto.NewsResponse;
import bd.edu.seu.gamesclub.entity.News;
import bd.edu.seu.gamesclub.entity.enums.PublishStatus;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.NewsMapper;
import bd.edu.seu.gamesclub.repository.NewsRepository;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.NewsService;
import bd.edu.seu.gamesclub.util.SlugUtil;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link NewsService}. */
@Service
@Transactional(readOnly = true)
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final MediaService mediaService;

    public NewsServiceImpl(NewsRepository newsRepository, MediaService mediaService) {
        this.newsRepository = newsRepository;
        this.mediaService = mediaService;
    }

    @Override
    public List<NewsResponse> getPublished() {
        return newsRepository.findByStatusOrderByPublishDateDesc(PublishStatus.PUBLISHED).stream()
                .map(NewsMapper::toResponse).toList();
    }

    @Override
    public Page<NewsResponse> getPublishedPage(Pageable pageable) {
        return newsRepository.findByStatus(PublishStatus.PUBLISHED, pageable).map(NewsMapper::toResponse);
    }

    @Override
    public Page<NewsResponse> search(String title, Pageable pageable) {
        return newsRepository.findByTitleContainingIgnoreCase(title, pageable).map(NewsMapper::toResponse);
    }

    @Override
    public NewsResponse getBySlug(String slug) {
        return newsRepository.findBySlug(slug).map(NewsMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("News", "slug", slug));
    }

    @Override
    public NewsResponse getById(Long id) {
        return NewsMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public NewsResponse create(NewsRequest request) {
        News news = new News();
        NewsMapper.apply(news, request);
        news.setSlug(SlugUtil.uniqueSlug(request.title(), newsRepository::existsBySlug));
        news.setImage(mediaService.getReference(request.imageMediaId()));
        return NewsMapper.toResponse(newsRepository.save(news));
    }

    @Override
    @Transactional
    public NewsResponse update(Long id, NewsRequest request) {
        News news = findEntity(id);
        NewsMapper.apply(news, request);
        news.setSlug(SlugUtil.uniqueSlug(request.title(),
                s -> newsRepository.existsBySlug(s) && !s.equals(news.getSlug())));
        news.setImage(mediaService.getReference(request.imageMediaId()));
        return NewsMapper.toResponse(newsRepository.save(news));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        newsRepository.delete(findEntity(id));
    }

    private News findEntity(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
    }
}
