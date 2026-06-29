package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.BroadcastRequest;
import bd.edu.seu.gamesclub.dto.EmailBroadcastResponse;
import bd.edu.seu.gamesclub.entity.EmailBroadcast;
import bd.edu.seu.gamesclub.entity.EmailBroadcastRecipient;
import bd.edu.seu.gamesclub.entity.MembershipPeriod;
import bd.edu.seu.gamesclub.entity.User;
import bd.edu.seu.gamesclub.entity.enums.ApplicationStatus;
import bd.edu.seu.gamesclub.entity.enums.BroadcastStatus;
import bd.edu.seu.gamesclub.entity.enums.BroadcastTarget;
import bd.edu.seu.gamesclub.entity.enums.DeliveryStatus;
import bd.edu.seu.gamesclub.entity.enums.MembershipStatus;
import bd.edu.seu.gamesclub.entity.enums.Role;
import bd.edu.seu.gamesclub.exception.ValidationException;
import bd.edu.seu.gamesclub.mapper.BroadcastMapper;
import bd.edu.seu.gamesclub.repository.EmailBroadcastRepository;
import bd.edu.seu.gamesclub.repository.MembershipApplicationRepository;
import bd.edu.seu.gamesclub.repository.MembershipPeriodRepository;
import bd.edu.seu.gamesclub.repository.UserRepository;
import bd.edu.seu.gamesclub.service.EmailBroadcastService;
import bd.edu.seu.gamesclub.service.EmailService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link EmailBroadcastService}.
 */
@Service
@Transactional(readOnly = true)
public class EmailBroadcastServiceImpl implements EmailBroadcastService {

    private final EmailBroadcastRepository broadcastRepository;
    private final UserRepository userRepository;
    private final MembershipPeriodRepository periodRepository;
    private final MembershipApplicationRepository applicationRepository;
    private final EmailService emailService;

    public EmailBroadcastServiceImpl(EmailBroadcastRepository broadcastRepository,
                                     UserRepository userRepository,
                                     MembershipPeriodRepository periodRepository,
                                     MembershipApplicationRepository applicationRepository,
                                     EmailService emailService) {
        this.broadcastRepository = broadcastRepository;
        this.userRepository = userRepository;
        this.periodRepository = periodRepository;
        this.applicationRepository = applicationRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public EmailBroadcastResponse send(BroadcastRequest request, String adminEmail) {
        BroadcastTarget target = BroadcastTarget.valueOf(request.targetType());
        Set<User> recipients = resolveRecipients(target, request.recipientUserIds());
        if (recipients.isEmpty()) {
            throw new ValidationException("No recipients matched the selected target.");
        }

        EmailBroadcast broadcast = new EmailBroadcast();
        broadcast.setSubject(request.subject());
        broadcast.setBody(request.body());
        broadcast.setTargetType(target);
        broadcast.setStatus(BroadcastStatus.PENDING);

        List<EmailBroadcastRecipient> rows = new ArrayList<>();
        for (User user : recipients) {
            if (user.getEmail() == null) {
                continue;
            }
            EmailBroadcastRecipient row = new EmailBroadcastRecipient();
            row.setBroadcast(broadcast);
            row.setRecipient(user);
            row.setRecipientEmail(user.getEmail());
            row.setDeliveryStatus(DeliveryStatus.SENT);
            row.setSentAt(LocalDateTime.now());
            rows.add(row);
            emailService.sendHtml(user.getEmail(), request.subject(), request.body());
        }
        broadcast.getRecipients().addAll(rows);
        broadcast.setRecipientCount(rows.size());
        broadcast.setStatus(BroadcastStatus.SENT);
        broadcast.setSentAt(LocalDateTime.now());

        return BroadcastMapper.toResponse(broadcastRepository.save(broadcast));
    }

    @Override
    public Page<EmailBroadcastResponse> history(Pageable pageable) {
        return broadcastRepository.findAllByOrderByCreatedAtDesc(pageable).map(BroadcastMapper::toResponse);
    }

    /** Resolves the recipient set for the chosen target. */
    private Set<User> resolveRecipients(BroadcastTarget target, List<Long> ids) {
        Set<User> recipients = new LinkedHashSet<>();
        switch (target) {
            case ALL_STUDENTS -> userRepository.findByRole(Role.ROLE_STUDENT).stream()
                    .filter(User::isEmailVerified).forEach(recipients::add);
            case SELECTED -> {
                if (ids != null && !ids.isEmpty()) {
                    recipients.addAll(userRepository.findAllById(ids));
                }
            }
            case MEMBERS_ONLY -> {
                MembershipPeriod period = periodRepository
                        .findFirstByStatusOrderByOpeningDateDesc(MembershipStatus.OPEN)
                        .orElseGet(() -> periodRepository.findFirstByOrderByOpeningDateDesc().orElse(null));
                if (period != null) {
                    applicationRepository.findByPeriodIdAndStatus(period.getId(), ApplicationStatus.APPROVED)
                            .forEach(app -> {
                                if (app.getStudent() != null) {
                                    recipients.add(app.getStudent());
                                }
                            });
                }
            }
        }
        return recipients;
    }
}
