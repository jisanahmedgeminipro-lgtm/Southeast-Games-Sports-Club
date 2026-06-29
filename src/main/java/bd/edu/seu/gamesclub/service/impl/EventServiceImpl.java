package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.EventRequest;
import bd.edu.seu.gamesclub.dto.EventResponse;
import bd.edu.seu.gamesclub.entity.Event;
import bd.edu.seu.gamesclub.entity.Sport;
import bd.edu.seu.gamesclub.entity.enums.EventStatus;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.EventMapper;
import bd.edu.seu.gamesclub.repository.EventRepository;
import bd.edu.seu.gamesclub.repository.SportRepository;
import bd.edu.seu.gamesclub.service.EventService;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.util.SlugUtil;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link EventService}. */
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final SportRepository sportRepository;
    private final MediaService mediaService;

    public EventServiceImpl(EventRepository eventRepository, SportRepository sportRepository, MediaService mediaService) {
        this.eventRepository = eventRepository;
        this.sportRepository = sportRepository;
        this.mediaService = mediaService;
    }

    @Override
    public List<EventResponse> getUpcoming() {
        return eventRepository.findByStatusOrderByEventDateAsc(EventStatus.UPCOMING).stream()
                .map(EventMapper::toResponse).toList();
    }

    @Override
    public Page<EventResponse> getPage(EventStatus status, Pageable pageable) {
        Page<Event> page = (status == null)
                ? eventRepository.findAll(pageable)
                : eventRepository.findByStatus(status, pageable);
        return page.map(EventMapper::toResponse);
    }

    @Override
    public Page<EventResponse> search(String title, Pageable pageable) {
        return eventRepository.findByTitleContainingIgnoreCase(title, pageable).map(EventMapper::toResponse);
    }

    @Override
    public EventResponse getBySlug(String slug) {
        return eventRepository.findBySlug(slug).map(EventMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "slug", slug));
    }

    @Override
    public EventResponse getById(Long id) {
        return EventMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public EventResponse create(EventRequest request) {
        Event event = new Event();
        EventMapper.apply(event, request);
        event.setSlug(SlugUtil.uniqueSlug(request.title(), eventRepository::existsBySlug));
        event.setBanner(mediaService.getReference(request.bannerMediaId()));
        event.setSport(resolveSport(request.sportId()));
        return EventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event event = findEntity(id);
        EventMapper.apply(event, request);
        event.setSlug(SlugUtil.uniqueSlug(request.title(),
                s -> eventRepository.existsBySlug(s) && !s.equals(event.getSlug())));
        event.setBanner(mediaService.getReference(request.bannerMediaId()));
        event.setSport(resolveSport(request.sportId()));
        return EventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        eventRepository.delete(findEntity(id));
    }

    private Sport resolveSport(Long sportId) {
        if (sportId == null) {
            return null;
        }
        return sportRepository.findById(sportId)
                .orElseThrow(() -> new ResourceNotFoundException("Sport", "id", sportId));
    }

    private Event findEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
    }
}
