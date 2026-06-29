package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.SportRequest;
import bd.edu.seu.gamesclub.dto.SportResponse;
import java.util.List;

/** Manages sports. Sport names must be unique. */
public interface SportService {

    /** Active sports for public display, ordered by display order. */
    List<SportResponse> getActive();

    /** All sports for admin management, ordered by display order. */
    List<SportResponse> getAll();

    /** Fetch a sport by id. */
    SportResponse getById(Long id);

    /** Create a sport (validates unique name). */
    SportResponse create(SportRequest request);

    /** Update a sport (validates unique name). */
    SportResponse update(Long id, SportRequest request);

    /** Delete a sport. */
    void delete(Long id);
}
