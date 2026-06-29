package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.ForgotPasswordRequest;
import bd.edu.seu.gamesclub.dto.OtpVerifyRequest;
import bd.edu.seu.gamesclub.dto.RegisterRequest;
import bd.edu.seu.gamesclub.dto.ResetPasswordRequest;
import bd.edu.seu.gamesclub.entity.StudentProfile;
import bd.edu.seu.gamesclub.entity.User;
import bd.edu.seu.gamesclub.entity.enums.Gender;
import bd.edu.seu.gamesclub.entity.enums.OtpPurpose;
import bd.edu.seu.gamesclub.entity.enums.Role;
import bd.edu.seu.gamesclub.exception.DuplicateResourceException;
import bd.edu.seu.gamesclub.exception.EmailDomainNotAllowedException;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.exception.ValidationException;
import bd.edu.seu.gamesclub.repository.StudentProfileRepository;
import bd.edu.seu.gamesclub.repository.UserRepository;
import bd.edu.seu.gamesclub.service.AuthService;
import bd.edu.seu.gamesclub.service.OtpService;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link AuthService} implementation.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final String allowedDomain;

    public AuthServiceImpl(UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           PasswordEncoder passwordEncoder,
                           OtpService otpService,
                           @Value("${app.registration.allowed-email-domain:seu.edu.bd}") String allowedDomain) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.allowedDomain = allowedDomain;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        String email = normalize(request.email());
        validateDomain(email);
        if (!request.password().equals(request.confirmPassword())) {
            throw new ValidationException("Passwords do not match.");
        }

        // Email uniqueness: block only when a verified account already owns it.
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && user.isEmailVerified()) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }

        // Student id uniqueness (ignore a record owned by this same unverified email).
        studentProfileRepository.findByStudentId(request.studentId()).ifPresent(p -> {
            boolean sameOwner = p.getUser() != null && email.equalsIgnoreCase(p.getUser().getEmail());
            if (!sameOwner) {
                throw new DuplicateResourceException("This Student ID is already registered.");
            }
        });

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setRole(Role.ROLE_STUDENT);
        }
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmailVerified(false);
        user.setEnabled(true);
        final User savedUser = userRepository.save(user);

        StudentProfile profile = studentProfileRepository.findByUserId(savedUser.getId())
                .orElseGet(StudentProfile::new);
        profile.setUser(savedUser);
        profile.setFullName(request.fullName());
        profile.setStudentId(request.studentId());
        profile.setDepartment(request.department());
        profile.setBatch(request.batch());
        profile.setSemester(request.semester());
        profile.setPhone(request.phone());
        profile.setGender(Gender.valueOf(request.gender()));
        profile.setActive(true);
        studentProfileRepository.save(profile);

        otpService.generateAndSend(email, OtpPurpose.REGISTRATION);
    }

    @Override
    @Transactional
    public void verifyRegistration(OtpVerifyRequest request) {
        String email = normalize(request.email());
        otpService.verify(email, request.otp(), OtpPurpose.REGISTRATION);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "email", email));
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resendRegistrationOtp(String email) {
        otpService.generateAndSend(normalize(email), OtpPurpose.REGISTRATION);
    }

    @Override
    @Transactional
    public void initiatePasswordReset(ForgotPasswordRequest request) {
        String email = normalize(request.email());
        // Silent for unknown emails to avoid account enumeration.
        userRepository.findByEmail(email)
                .ifPresent(u -> otpService.generateAndSend(email, OtpPurpose.FORGOT_PASSWORD));
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = normalize(request.email());
        if (!request.password().equals(request.confirmPassword())) {
            throw new ValidationException("Passwords do not match.");
        }
        otpService.verify(email, request.otp(), OtpPurpose.FORGOT_PASSWORD);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "email", email));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    private void validateDomain(String email) {
        if (!email.endsWith("@" + allowedDomain.toLowerCase(Locale.ENGLISH))) {
            throw new EmailDomainNotAllowedException(
                    "Registration is restricted to @" + allowedDomain + " email addresses.");
        }
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ENGLISH);
    }
}
