package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.FaqRequest;
import bd.edu.seu.gamesclub.dto.FaqResponse;
import java.util.List;

/** Manages FAQ entries. */
public interface FaqService {

    List<FaqResponse> getActive();

    List<FaqResponse> getAll();

    FaqResponse getById(Long id);

    FaqResponse create(FaqRequest request);

    FaqResponse update(Long id, FaqRequest request);

    void delete(Long id);
}
