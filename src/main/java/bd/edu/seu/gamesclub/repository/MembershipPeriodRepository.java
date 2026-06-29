package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.MembershipPeriod;
import bd.edu.seu.gamesclub.entity.enums.MembershipStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link MembershipPeriod} drives.
 */
@Repository
public interface MembershipPeriodRepository extends JpaRepository<MembershipPeriod, Long> {

    /** Find the active membership period (the latest one with the given status, e.g. OPEN). */
    Optional<MembershipPeriod> findFirstByStatusOrderByOpeningDateDesc(MembershipStatus status);

    /** Find the most recent period overall - source of the current announcement to display. */
    Optional<MembershipPeriod> findFirstByOrderByOpeningDateDesc();

    /** Whether any period currently has the given status (e.g. is membership OPEN). */
    boolean existsByStatus(MembershipStatus status);

    /** Full history, newest drive first. */
    List<MembershipPeriod> findAllByOrderByOpeningDateDesc();
}
