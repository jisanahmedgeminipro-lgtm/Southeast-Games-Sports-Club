package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.ContactMessageResponse;
import bd.edu.seu.gamesclub.dto.ContactRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Handles public contact-form submissions and admin review. */
public interface ContactService {

    /** Persist a visitor submission. */
    void submit(ContactRequest request, String ipAddress);

    /** Paginated inbox, newest first. */
    Page<ContactMessageResponse> list(Pageable pageable);

    /** Unread messages. */
    List<ContactMessageResponse> unread();

    ContactMessageResponse getById(Long id);

    /** Mark a message as read. */
    void markRead(Long id);

    void delete(Long id);

    /** Count of unread messages (dashboard badge). */
    long countUnread();
}
