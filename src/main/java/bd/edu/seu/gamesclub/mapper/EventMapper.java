package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.EventRequest;
import bd.edu.seu.gamesclub.dto.EventResponse;
import bd.edu.seu.gamesclub.entity.Event;
import bd.edu.seu.gamesclub.entity.enums.EventStatus;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link Event}. Banner and sport relations are resolved by the service. */
public final class EventMapper {

    private EventMapper() {
    }

    public static EventResponse toResponse(Event e) {
        if (e == null) {
            return null;
        }
        return new EventResponse(
                e.getId(),
                e.getTitle(),
                e.getSlug(),
                e.getDescription(),
                MediaUrls.url(e.getBanner()),
                e.getVenue(),
                e.getEventDate(),
                e.getEventTime(),
                e.getRegistrationDeadline(),
                e.getStatus() != null ? e.getStatus().name() : null,
                e.getSport() != null ? e.getSport().getId() : null,
                e.getSport() != null ? e.getSport().getName() : null,
                e.getBanner() != null ? e.getBanner().getId() : null
        );
    }

    /** Copies scalar fields (including status) from the request onto the entity. */
    public static void apply(Event e, EventRequest r) {
        e.setTitle(r.title());
        e.setDescription(r.description());
        e.setVenue(r.venue());
        e.setEventDate(r.eventDate());
        e.setEventTime(r.eventTime());
        e.setRegistrationDeadline(r.registrationDeadline());
        if (r.status() != null && !r.status().isBlank()) {
            e.setStatus(EventStatus.valueOf(r.status()));
        }
    }
}
