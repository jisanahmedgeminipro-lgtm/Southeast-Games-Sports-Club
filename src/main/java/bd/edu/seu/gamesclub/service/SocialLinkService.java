package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.SocialLinkRequest;
import bd.edu.seu.gamesclub.dto.SocialLinkResponse;
import java.util.List;

/** Manages social media links. Platform must be unique. */
public interface SocialLinkService {

    /** Active links ordered by display order (header/footer). */
    List<SocialLinkResponse> getActive();

    List<SocialLinkResponse> getAll();

    SocialLinkResponse getById(Long id);

    SocialLinkResponse create(SocialLinkRequest request);

    SocialLinkResponse update(Long id, SocialLinkRequest request);

    void delete(Long id);
}
