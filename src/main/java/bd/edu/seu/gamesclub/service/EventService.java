package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.EventRequest;
import bd.edu.seu.gamesclub.dto.EventResponse;
import bd.edu.seu.gamesclub.entity.enums.EventStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Manages events. */
public interface EventService {

    /** Upcoming events ordered by date (public landing). */
    List<EventResponse> getUpcoming();

    /** Paginated events, optionally filtered by status. */
    Page<EventResponse> getPage(EventStatus status, Pageable pageable);

    /** Paginated title search. */
    Page<EventResponse> search(String title, Pageable pageable);

    /** Fetch an event by slug. */
    EventResponse getBySlug(String slug);

    /** Fetch an event by id. */
    EventResponse getById(Long id);

    EventResponse create(EventRequest request);

    EventResponse update(Long id, EventRequest request);

    void delete(Long id);
}
