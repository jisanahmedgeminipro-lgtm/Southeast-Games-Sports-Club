package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.HeroSliderRequest;
import bd.edu.seu.gamesclub.dto.HeroSliderResponse;
import java.util.List;

/** Manages landing hero slides. Display order must remain unique. */
public interface HeroSliderService {

    /** Active slides ordered by display order (public hero). */
    List<HeroSliderResponse> getActive();

    /** All slides (admin). */
    List<HeroSliderResponse> getAll();

    HeroSliderResponse getById(Long id);

    /** Create a slide (validates unique display order). */
    HeroSliderResponse create(HeroSliderRequest request);

    /** Update a slide (validates unique display order). */
    HeroSliderResponse update(Long id, HeroSliderRequest request);

    void delete(Long id);
}
