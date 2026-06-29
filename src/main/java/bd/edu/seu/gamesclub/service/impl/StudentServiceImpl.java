package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.StudentResponse;
import bd.edu.seu.gamesclub.dto.StudentUpdateRequest;
import bd.edu.seu.gamesclub.entity.StudentProfile;
import bd.edu.seu.gamesclub.entity.enums.Gender;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.StudentMapper;
import bd.edu.seu.gamesclub.repository.StudentProfileRepository;
import bd.edu.seu.gamesclub.service.MediaService;
import bd.edu.seu.gamesclub.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link StudentService}.
 */
@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final MediaService mediaService;

    public StudentServiceImpl(StudentProfileRepository studentProfileRepository, MediaService mediaService) {
        this.studentProfileRepository = studentProfileRepository;
        this.mediaService = mediaService;
    }

    @Override
    public Page<StudentResponse> findAll(Pageable pageable) {
        return studentProfileRepository.findAll(pageable).map(StudentMapper::toResponse);
    }

    @Override
    public Page<StudentResponse> searchByName(String name, Pageable pageable) {
        return studentProfileRepository.findByFullNameContainingIgnoreCase(name, pageable).map(StudentMapper::toResponse);
    }

    @Override
    public Page<StudentResponse> searchByStudentId(String studentId, Pageable pageable) {
        return studentProfileRepository.findByStudentIdContainingIgnoreCase(studentId, pageable).map(StudentMapper::toResponse);
    }

    @Override
    public Page<StudentResponse> filterByDepartment(String department, Pageable pageable) {
        return studentProfileRepository.findByDepartmentIgnoreCase(department, pageable).map(StudentMapper::toResponse);
    }

    @Override
    public Page<StudentResponse> filterByBatch(String batch, Pageable pageable) {
        return studentProfileRepository.findByBatch(batch, pageable).map(StudentMapper::toResponse);
    }

    @Override
    public StudentResponse getById(Long id) {
        return StudentMapper.toResponse(findEntity(id));
    }

    @Override
    public StudentResponse getByEmail(String email) {
        StudentProfile profile = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile", "email", email));
        return StudentMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public StudentResponse updateOwnProfile(String email, StudentUpdateRequest request) {
        StudentProfile profile = studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile", "email", email));
        profile.setFullName(request.fullName());
        profile.setDepartment(request.department());
        profile.setBatch(request.batch());
        profile.setSemester(request.semester());
        profile.setPhone(request.phone());
        profile.setGender(Gender.valueOf(request.gender()));
        if (request.profileMediaId() != null) {
            profile.setProfilePicture(mediaService.getReference(request.profileMediaId()));
        }
        return StudentMapper.toResponse(studentProfileRepository.save(profile));
    }

    @Override
    @Transactional
    public void setActive(Long id, boolean active) {
        StudentProfile profile = findEntity(id);
        profile.setActive(active);
        studentProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StudentProfile profile = findEntity(id);
        studentProfileRepository.delete(profile);
    }

    private StudentProfile findEntity(Long id) {
        return studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile", "id", id));
    }
}
