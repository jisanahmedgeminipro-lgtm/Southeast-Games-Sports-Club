package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.CommitteeMember;
import bd.edu.seu.gamesclub.entity.enums.CommitteeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link CommitteeMember}s. The same methods serve both the
 * Executive and Sub-Executive committees by passing the appropriate
 * {@link CommitteeType}.
 */
@Repository
public interface CommitteeMemberRepository extends JpaRepository<CommitteeMember, Long> {

    /** Public view: active members of a committee, ordered by display order. */
    List<CommitteeMember> findByCommitteeTypeAndActiveTrueOrderByDisplayOrderAsc(CommitteeType committeeType);

    /** Admin view: all members of a committee (active or not), ordered by display order. */
    List<CommitteeMember> findByCommitteeTypeOrderByDisplayOrderAsc(CommitteeType committeeType);

    /** Search committee members by (partial, case-insensitive) name. */
    List<CommitteeMember> findByNameContainingIgnoreCase(String name);
}
