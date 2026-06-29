package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.CommitteeMemberRequest;
import bd.edu.seu.gamesclub.dto.CommitteeMemberResponse;
import bd.edu.seu.gamesclub.entity.enums.CommitteeType;
import java.util.List;

/** Manages Executive and Sub-Executive committee members (one table, by type). */
public interface CommitteeService {

    /** Active members of a committee, ordered by display order (public). */
    List<CommitteeMemberResponse> getActiveByType(CommitteeType type);

    /** All members of a committee (admin). */
    List<CommitteeMemberResponse> getAllByType(CommitteeType type);

    /** Search members by name. */
    List<CommitteeMemberResponse> searchByName(String name);

    CommitteeMemberResponse getById(Long id);

    CommitteeMemberResponse create(CommitteeMemberRequest request);

    CommitteeMemberResponse update(Long id, CommitteeMemberRequest request);

    void delete(Long id);
}
