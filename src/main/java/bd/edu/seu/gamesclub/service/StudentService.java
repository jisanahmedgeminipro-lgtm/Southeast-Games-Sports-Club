package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.StudentResponse;
import bd.edu.seu.gamesclub.dto.StudentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Student profile management for both the admin (listing, search, activation,
 * deletion) and the student (viewing/editing their own profile).
 */
public interface StudentService {

    /** Paginated list of all students. */
    Page<StudentResponse> findAll(Pageable pageable);

    /** Paginated search by full name. */
    Page<StudentResponse> searchByName(String name, Pageable pageable);

    /** Paginated search by student id. */
    Page<StudentResponse> searchByStudentId(String studentId, Pageable pageable);

    /** Paginated filter by department. */
    Page<StudentResponse> filterByDepartment(String department, Pageable pageable);

    /** Paginated filter by batch. */
    Page<StudentResponse> filterByBatch(String batch, Pageable pageable);

    /** Fetch a single student profile by id. */
    StudentResponse getById(Long id);

    /** Fetch a student profile by the owning account email. */
    StudentResponse getByEmail(String email);

    /** Update the authenticated student's own editable profile fields. */
    StudentResponse updateOwnProfile(String email, StudentUpdateRequest request);

    /** Admin: enable/disable a student profile. */
    void setActive(Long id, boolean active);

    /** Admin: delete a student profile (and cascade the account). */
    void delete(Long id);
}
