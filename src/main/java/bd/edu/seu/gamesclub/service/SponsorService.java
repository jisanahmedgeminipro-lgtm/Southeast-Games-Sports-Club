package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.SponsorRequest;
import bd.edu.seu.gamesclub.dto.SponsorResponse;
import java.util.List;

/** Manages sponsors. */
public interface SponsorService {

    List<SponsorResponse> getActive();

    List<SponsorResponse> getAll();

    SponsorResponse getById(Long id);

    SponsorResponse create(SponsorRequest request);

    SponsorResponse update(Long id, SponsorRequest request);

    void delete(Long id);
}
