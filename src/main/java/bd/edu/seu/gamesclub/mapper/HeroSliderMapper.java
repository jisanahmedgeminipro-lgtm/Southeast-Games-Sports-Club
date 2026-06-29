package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.HeroSliderRequest;
import bd.edu.seu.gamesclub.dto.HeroSliderResponse;
import bd.edu.seu.gamesclub.entity.HeroSlider;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link HeroSlider}. Background media resolved by the service. */
public final class HeroSliderMapper {

    private HeroSliderMapper() {
    }

    public static HeroSliderResponse toResponse(HeroSlider h) {
        if (h == null) {
            return null;
        }
        return new HeroSliderResponse(
                h.getId(), h.getTitle(), h.getSubtitle(), h.getDescription(),
                h.getButtonText(), h.getButtonUrl(), MediaUrls.url(h.getBackground()),
                h.getDisplayOrder(), h.isActive(),
                h.getBackground() != null ? h.getBackground().getId() : null
        );
    }

    public static void apply(HeroSlider h, HeroSliderRequest r) {
        h.setTitle(r.title());
        h.setSubtitle(r.subtitle());
        h.setDescription(r.description());
        h.setButtonText(r.buttonText());
        h.setButtonUrl(r.buttonUrl());
        if (r.displayOrder() != null) {
            h.setDisplayOrder(r.displayOrder());
        }
        if (r.active() != null) {
            h.setActive(r.active());
        }
    }
}
