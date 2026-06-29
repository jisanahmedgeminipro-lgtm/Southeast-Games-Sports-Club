package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.StudentProfile;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link StudentProfile} records.
 *
 * <p>Provides paginated listing and the student search facets (name, student id,
 * department, batch). The total-students dashboard count is the inherited
 * {@link JpaRepository#count()}.
 */
@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    /** Find a profile by its unique university student id. */
    Optional<StudentProfile> findByStudentId(String studentId);

    /** Find a profile by the owning user's id. */
    Optional<StudentProfile> findByUserId(Long userId);

    /** Find a profile by the owning user's email. */
    Optional<StudentProfile> findByUserEmail(String email);

    /** Whether a profile already uses the given student id. */
    boolean existsByStudentId(String studentId);

    /** Paginated list of active students. */
    Page<StudentProfile> findByActiveTrue(Pageable pageable);

    /** Search by (partial, case-insensitive) full name. */
    Page<StudentProfile> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    /** Search by (partial, case-insensitive) student id. */
    Page<StudentProfile> findByStudentIdContainingIgnoreCase(String studentId, Pageable pageable);

    /** Filter by department. */
    Page<StudentProfile> findByDepartmentIgnoreCase(String department, Pageable pageable);

    /** Filter by batch. */
    Page<StudentProfile> findByBatch(String batch, Pageable pageable);
}
