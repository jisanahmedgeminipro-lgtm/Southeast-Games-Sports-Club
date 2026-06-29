package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.SocialLinkRequest;
import bd.edu.seu.gamesclub.dto.SocialLinkResponse;
import bd.edu.seu.gamesclub.entity.SocialLink;

/** Manual mapper for {@link SocialLink}. */
public final class SocialLinkMapper {

    private SocialLinkMapper() {
    }

    public static SocialLinkResponse toResponse(SocialLink s) {
        if (s == null) {
            return null;
        }
        return new SocialLinkResponse(s.getId(), s.getPlatform(), s.getUrl(),
                s.getIconClass(), s.getDisplayOrder(), s.isActive());
    }

    public static void apply(SocialLink s, SocialLinkRequest r) {
        s.setPlatform(r.platform());
        s.setUrl(r.url());
        s.setIconClass(r.iconClass());
        if (r.displayOrder() != null) {
            s.setDisplayOrder(r.displayOrder());
        }
        if (r.active() != null) {
            s.setActive(r.active());
        }
    }
}
