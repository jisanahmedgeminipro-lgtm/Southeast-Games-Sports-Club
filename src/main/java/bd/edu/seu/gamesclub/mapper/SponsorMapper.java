package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.SponsorRequest;
import bd.edu.seu.gamesclub.dto.SponsorResponse;
import bd.edu.seu.gamesclub.entity.Sponsor;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link Sponsor}. Logo media resolved by the service. */
public final class SponsorMapper {

    private SponsorMapper() {
    }

    public static SponsorResponse toResponse(Sponsor s) {
        if (s == null) {
            return null;
        }
        return new SponsorResponse(s.getId(), s.getName(), MediaUrls.url(s.getLogo()),
                s.getWebsite(), s.getDisplayOrder(), s.isActive(),
                s.getLogo() != null ? s.getLogo().getId() : null);
    }

    public static void apply(Sponsor s, SponsorRequest r) {
        s.setName(r.name());
        s.setWebsite(r.website());
        if (r.displayOrder() != null) {
            s.setDisplayOrder(r.displayOrder());
        }
        if (r.active() != null) {
            s.setActive(r.active());
        }
    }
}
