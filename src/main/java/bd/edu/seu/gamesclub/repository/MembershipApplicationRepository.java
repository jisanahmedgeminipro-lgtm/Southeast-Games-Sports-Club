package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.MembershipApplication;
import bd.edu.seu.gamesclub.entity.enums.ApplicationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link MembershipApplication}s.
 *
 * <p>Total applications is the inherited {@link JpaRepository#count()};
 * total pending is {@link #countByStatus(ApplicationStatus)}.
 */
@Repository
public interface MembershipApplicationRepository extends JpaRepository<MembershipApplication, Long> {

    /** All applications with the given status (pending / approved / rejected). */
    List<MembershipApplication> findByStatus(ApplicationStatus status);

    /** Applications of a given status within a specific drive (e.g. approved members). */
    List<MembershipApplication> findByPeriodIdAndStatus(Long periodId, ApplicationStatus status);

    /** A student's application history, newest first. */
    List<MembershipApplication> findByStudentIdOrderByAppliedAtDesc(Long studentUserId);

    /** Guard: a student may apply only once per drive. */
    boolean existsByPeriodIdAndStudentId(Long periodId, Long studentUserId);

    /** Count of applications by status (dashboard: total pending applications). */
    long countByStatus(ApplicationStatus status);
}
