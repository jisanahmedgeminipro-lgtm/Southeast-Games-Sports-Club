package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.ContactMessage;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for visitor {@link ContactMessage}s. Total messages is the inherited
 * {@link JpaRepository#count()}.
 */
@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    /** Paginated inbox, newest first. */
    Page<ContactMessage> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** Unread messages, newest first. */
    List<ContactMessage> findByReadFalseOrderByCreatedAtDesc();

    /** Count of unread messages (dashboard badge). */
    long countByReadFalse();
}
