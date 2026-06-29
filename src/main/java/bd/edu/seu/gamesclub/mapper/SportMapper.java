package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.SportRequest;
import bd.edu.seu.gamesclub.dto.SportResponse;
import bd.edu.seu.gamesclub.entity.Sport;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link Sport}. Relations (icon/image media) are resolved by the service. */
public final class SportMapper {

    private SportMapper() {
    }

    public static SportResponse toResponse(Sport s) {
        if (s == null) {
            return null;
        }
        return new SportResponse(
                s.getId(),
                s.getName(),
                s.getSlug(),
                s.getDescription(),
                MediaUrls.url(s.getIcon()),
                MediaUrls.url(s.getImage()),
                s.getIcon() != null ? s.getIcon().getId() : null,
                s.getImage() != null ? s.getImage().getId() : null,
                s.getDisplayOrder(),
                s.isActive()
        );
    }

    /** Copies scalar fields from the request onto the entity. */
    public static void apply(Sport s, SportRequest r) {
        s.setName(r.name());
        s.setDescription(r.description());
        if (r.displayOrder() != null) {
            s.setDisplayOrder(r.displayOrder());
        }
        if (r.active() != null) {
            s.setActive(r.active());
        }
    }
}
