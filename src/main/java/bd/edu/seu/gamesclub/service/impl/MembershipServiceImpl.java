package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.MembershipApplicationResponse;
import bd.edu.seu.gamesclub.dto.MembershipPeriodRequest;
import bd.edu.seu.gamesclub.dto.MembershipPeriodResponse;
import bd.edu.seu.gamesclub.entity.MembershipApplication;
import bd.edu.seu.gamesclub.entity.MembershipPeriod;
import bd.edu.seu.gamesclub.entity.User;
import bd.edu.seu.gamesclub.entity.enums.ApplicationStatus;
import bd.edu.seu.gamesclub.entity.enums.MembershipStatus;
import bd.edu.seu.gamesclub.entity.enums.Role;
import bd.edu.seu.gamesclub.exception.DuplicateApplicationException;
import bd.edu.seu.gamesclub.exception.MembershipNotOpenException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.exception.ValidationException;
import bd.edu.seu.gamesclub.mapper.MembershipMapper;
import bd.edu.seu.gamesclub.repository.MembershipApplicationRepository;
import bd.edu.seu.gamesclub.repository.MembershipPeriodRepository;
import bd.edu.seu.gamesclub.repository.StudentProfileRepository;
import bd.edu.seu.gamesclub.repository.UserRepository;
import bd.edu.seu.gamesclub.service.ActivityLogService;
import bd.edu.seu.gamesclub.service.EmailService;
import bd.edu.seu.gamesclub.service.MembershipService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link MembershipService}.
 */
@Service
@Transactional(readOnly = true)
public class MembershipServiceImpl implements MembershipService {

    private final MembershipPeriodRepository periodRepository;
    private final MembershipApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    public MembershipServiceImpl(MembershipPeriodRepository periodRepository,
                                 MembershipApplicationRepository applicationRepository,
                                 UserRepository userRepository,
                                 StudentProfileRepository studentProfileRepository,
                                 EmailService emailService,
                                 ActivityLogService activityLogService) {
        this.periodRepository = periodRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.emailService = emailService;
        this.activityLogService = activityLogService;
    }

    /* ----------------------------- Period management ----------------------------- */

    @Override
    @Transactional
    public MembershipPeriodResponse createPeriod(MembershipPeriodRequest request) {
        validateDates(request);
        MembershipPeriod period = new MembershipPeriod();
        applyRequest(period, request);
        period.setStatus(MembershipStatus.DRAFT);
        return MembershipMapper.toPeriodResponse(periodRepository.save(period));
    }

    @Override
    @Transactional
    public MembershipPeriodResponse updatePeriod(Long id, MembershipPeriodRequest request) {
        validateDates(request);
        MembershipPeriod period = findPeriod(id);
        applyRequest(period, request);
        return MembershipMapper.toPeriodResponse(periodRepository.save(period));
    }

    @Override
    @Transactional
    public void open(Long periodId, String adminEmail) {
        MembershipPeriod period = findPeriod(periodId);
        period.setStatus(MembershipStatus.OPEN);
        period.setOpenedAt(LocalDateTime.now());
        periodRepository.save(period);
        notifyStudents(period);
        activityLogService.log("MEMBERSHIP_OPENED", "MembershipPeriod", period.getId(),
                "Membership period '" + period.getTitle() + "' opened by " + adminEmail);
    }

    @Override
    @Transactional
    public void close(Long periodId, String adminEmail) {
        MembershipPeriod period = findPeriod(periodId);
        period.setStatus(MembershipStatus.CLOSED);
        period.setClosedAt(LocalDateTime.now());
        periodRepository.save(period);
        activityLogService.log("MEMBERSHIP_CLOSED", "MembershipPeriod", period.getId(),
                "Membership period '" + period.getTitle() + "' closed by " + adminEmail);
    }

    @Override
    public List<MembershipPeriodResponse> getAllPeriods() {
        return periodRepository.findAllByOrderByOpeningDateDesc().stream()
                .map(MembershipMapper::toPeriodResponse).toList();
    }

    @Override
    public MembershipPeriodResponse getActivePeriod() {
        return periodRepository.findFirstByStatusOrderByOpeningDateDesc(MembershipStatus.OPEN)
                .map(MembershipMapper::toPeriodResponse).orElse(null);
    }

    @Override
    public MembershipPeriodResponse getCurrentPeriod() {
        return periodRepository.findFirstByOrderByOpeningDateDesc()
                .map(MembershipMapper::toPeriodResponse).orElse(null);
    }

    @Override
    public boolean isOpen() {
        return periodRepository.existsByStatus(MembershipStatus.OPEN);
    }

    /* ----------------------------- Applications ----------------------------- */

    @Override
    @Transactional
    public MembershipApplicationResponse apply(String studentEmail) {
        MembershipPeriod period = periodRepository.findFirstByStatusOrderByOpeningDateDesc(MembershipStatus.OPEN)
                .orElseThrow(() -> new MembershipNotOpenException("Membership is currently closed."));
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "email", studentEmail));

        if (applicationRepository.existsByPeriodIdAndStudentId(period.getId(), student.getId())) {
            throw new DuplicateApplicationException("You have already applied for this membership period.");
        }

        MembershipApplication application = new MembershipApplication();
        application.setPeriod(period);
        application.setStudent(student);
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());
        applicationRepository.save(application);
        return MembershipMapper.toApplicationResponse(application, resolveName(student.getId()));
    }

    @Override
    public MembershipApplicationResponse getMyApplication(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail).orElse(null);
        if (student == null) {
            return null;
        }
        return applicationRepository.findByStudentIdOrderByAppliedAtDesc(student.getId()).stream()
                .findFirst()
                .map(a -> MembershipMapper.toApplicationResponse(a, resolveName(student.getId())))
                .orElse(null);
    }

    @Override
    public List<MembershipApplicationResponse> getApplications(String status) {
        List<MembershipApplication> apps = (status == null || status.isBlank())
                ? applicationRepository.findAll()
                : applicationRepository.findByStatus(ApplicationStatus.valueOf(status));
        List<MembershipApplicationResponse> out = new ArrayList<>(apps.size());
        for (MembershipApplication a : apps) {
            out.add(MembershipMapper.toApplicationResponse(a,
                    a.getStudent() != null ? resolveName(a.getStudent().getId()) : null));
        }
        return out;
    }

    @Override
    @Transactional
    public void review(Long applicationId, boolean approve, String remarks, String adminEmail) {
        MembershipApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
        User admin = userRepository.findByEmail(adminEmail).orElse(null);
        application.setStatus(approve ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED);
        application.setReviewedBy(admin);
        application.setReviewedAt(LocalDateTime.now());
        application.setRemarks(remarks);
        applicationRepository.save(application);

        if (application.getStudent() != null && application.getStudent().getEmail() != null) {
            emailService.sendMembershipDecision(application.getStudent().getEmail(),
                    resolveName(application.getStudent().getId()), approve, remarks);
        }
    }

    /* ----------------------------- Scheduled hooks ----------------------------- */

    @Override
    @Transactional
    public int openDuePeriods() {
        LocalDate today = LocalDate.now();
        int opened = 0;
        for (MembershipPeriod period : periodRepository.findAllByOrderByOpeningDateDesc()) {
            if (period.getStatus() == MembershipStatus.DRAFT
                    && !period.getOpeningDate().isAfter(today)
                    && !period.getClosingDate().isBefore(today)) {
                period.setStatus(MembershipStatus.OPEN);
                period.setOpenedAt(LocalDateTime.now());
                periodRepository.save(period);
                notifyStudents(period);
                activityLogService.log("MEMBERSHIP_AUTO_OPENED", "MembershipPeriod", period.getId(),
                        "Membership period '" + period.getTitle() + "' auto-opened on schedule");
                opened++;
            }
        }
        return opened;
    }

    @Override
    @Transactional
    public int closeExpiredPeriods() {
        LocalDate today = LocalDate.now();
        int closed = 0;
        for (MembershipPeriod period : periodRepository.findAllByOrderByOpeningDateDesc()) {
            if (period.getStatus() == MembershipStatus.OPEN && period.getClosingDate().isBefore(today)) {
                period.setStatus(MembershipStatus.CLOSED);
                period.setClosedAt(LocalDateTime.now());
                periodRepository.save(period);
                activityLogService.log("MEMBERSHIP_AUTO_CLOSED", "MembershipPeriod", period.getId(),
                        "Membership period '" + period.getTitle() + "' auto-closed on schedule");
                closed++;
            }
        }
        return closed;
    }

    /* ----------------------------- Helpers ----------------------------- */

    /** Emails every email-verified student that membership has opened (idempotent). */
    private void notifyStudents(MembershipPeriod period) {
        if (period.isNotificationSent()) {
            return;
        }
        for (User student : userRepository.findByRole(Role.ROLE_STUDENT)) {
            if (student.isEmailVerified()) {
                emailService.sendMembershipOpened(student.getEmail(), resolveName(student.getId()), period);
            }
        }
        period.setNotificationSent(true);
        periodRepository.save(period);
    }

    private String resolveName(Long userId) {
        return studentProfileRepository.findByUserId(userId)
                .map(p -> p.getFullName())
                .orElse(null);
    }

    private MembershipPeriod findPeriod(Long id) {
        return periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPeriod", "id", id));
    }

    private void applyRequest(MembershipPeriod period, MembershipPeriodRequest request) {
        period.setTitle(request.title());
        period.setAnnouncement(request.announcement());
        period.setOpeningDate(request.openingDate());
        period.setClosingDate(request.closingDate());
    }

    private void validateDates(MembershipPeriodRequest request) {
        if (request.closingDate().isBefore(request.openingDate())) {
            throw new ValidationException("Closing date must be on or after the opening date.");
        }
    }
}
