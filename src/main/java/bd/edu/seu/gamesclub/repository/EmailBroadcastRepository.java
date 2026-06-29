package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.EmailBroadcast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link EmailBroadcast} campaign history.
 */
@Repository
public interface EmailBroadcastRepository extends JpaRepository<EmailBroadcast, Long> {

    /** Paginated broadcast history, newest first. */
    Page<EmailBroadcast> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
