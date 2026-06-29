package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.DashboardStatsResponse;
import bd.edu.seu.gamesclub.entity.enums.ApplicationStatus;
import bd.edu.seu.gamesclub.entity.enums.Role;
import bd.edu.seu.gamesclub.repository.ContactMessageRepository;
import bd.edu.seu.gamesclub.repository.EventRepository;
import bd.edu.seu.gamesclub.repository.GalleryImageRepository;
import bd.edu.seu.gamesclub.repository.MembershipApplicationRepository;
import bd.edu.seu.gamesclub.repository.NewsRepository;
import bd.edu.seu.gamesclub.repository.SportRepository;
import bd.edu.seu.gamesclub.repository.StudentProfileRepository;
import bd.edu.seu.gamesclub.repository.UserRepository;
import bd.edu.seu.gamesclub.service.DashboardService;
import bd.edu.seu.gamesclub.service.MembershipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link DashboardService}. */
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final StudentProfileRepository studentProfileRepository;
    private final EventRepository eventRepository;
    private final NewsRepository newsRepository;
    private final SportRepository sportRepository;
    private final GalleryImageRepository galleryImageRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final MembershipApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final MembershipService membershipService;

    public DashboardServiceImpl(StudentProfileRepository studentProfileRepository,
                                EventRepository eventRepository,
                                NewsRepository newsRepository,
                                SportRepository sportRepository,
                                GalleryImageRepository galleryImageRepository,
                                ContactMessageRepository contactMessageRepository,
                                MembershipApplicationRepository applicationRepository,
                                UserRepository userRepository,
                                MembershipService membershipService) {
        this.studentProfileRepository = studentProfileRepository;
        this.eventRepository = eventRepository;
        this.newsRepository = newsRepository;
        this.sportRepository = sportRepository;
        this.galleryImageRepository = galleryImageRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.membershipService = membershipService;
    }

    @Override
    public DashboardStatsResponse getStats() {
        return new DashboardStatsResponse(
                studentProfileRepository.count(),
                eventRepository.count(),
                newsRepository.count(),
                sportRepository.count(),
                galleryImageRepository.count(),
                contactMessageRepository.count(),
                contactMessageRepository.countByReadFalse(),
                applicationRepository.count(),
                applicationRepository.countByStatus(ApplicationStatus.PENDING),
                userRepository.countByRole(Role.ROLE_STUDENT),
                membershipService.isOpen()
        );
    }
}
