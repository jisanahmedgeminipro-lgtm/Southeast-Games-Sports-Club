package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.StudentResponse;
import bd.edu.seu.gamesclub.entity.StudentProfile;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link StudentProfile}. */
public final class StudentMapper {

    private StudentMapper() {
    }

    public static StudentResponse toResponse(StudentProfile p) {
        if (p == null) {
            return null;
        }
        return new StudentResponse(
                p.getId(),
                p.getUser() != null ? p.getUser().getId() : null,
                p.getFullName(),
                p.getStudentId(),
                p.getUser() != null ? p.getUser().getEmail() : null,
                p.getDepartment(),
                p.getBatch(),
                p.getSemester(),
                p.getPhone(),
                p.getGender() != null ? p.getGender().name() : null,
                p.isActive(),
                MediaUrls.url(p.getProfilePicture()),
                p.getCreatedAt()
        );
    }
}
