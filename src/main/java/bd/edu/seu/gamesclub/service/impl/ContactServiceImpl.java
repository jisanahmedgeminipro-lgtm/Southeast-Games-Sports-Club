package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.ContactMessageResponse;
import bd.edu.seu.gamesclub.dto.ContactRequest;
import bd.edu.seu.gamesclub.entity.ContactMessage;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.ContactMapper;
import bd.edu.seu.gamesclub.repository.ContactMessageRepository;
import bd.edu.seu.gamesclub.service.ContactService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link ContactService}. */
@Service
@Transactional(readOnly = true)
public class ContactServiceImpl implements ContactService {

    private final ContactMessageRepository contactMessageRepository;
    private final bd.edu.seu.gamesclub.service.EmailService emailService;

    public ContactServiceImpl(ContactMessageRepository contactMessageRepository,
                              bd.edu.seu.gamesclub.service.EmailService emailService) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void submit(ContactRequest request, String ipAddress) {
        ContactMessage message = ContactMapper.toEntity(request);
        message.setIpAddress(ipAddress);
        contactMessageRepository.save(message);
        emailService.sendContactAutoReply(request.email(), request.name());
    }

    @Override
    public Page<ContactMessageResponse> list(Pageable pageable) {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc(pageable).map(ContactMapper::toResponse);
    }

    @Override
    public List<ContactMessageResponse> unread() {
        return contactMessageRepository.findByReadFalseOrderByCreatedAtDesc().stream()
                .map(ContactMapper::toResponse).toList();
    }

    @Override
    public ContactMessageResponse getById(Long id) {
        return ContactMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public void markRead(Long id) {
        ContactMessage message = findEntity(id);
        message.setRead(true);
        contactMessageRepository.save(message);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        contactMessageRepository.delete(findEntity(id));
    }

    @Override
    public long countUnread() {
        return contactMessageRepository.countByReadFalse();
    }

    private ContactMessage findEntity(Long id) {
        return contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
    }
}
