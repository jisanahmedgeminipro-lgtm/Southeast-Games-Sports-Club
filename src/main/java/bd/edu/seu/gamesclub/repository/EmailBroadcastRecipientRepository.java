package bd.edu.seu.gamesclub.repository;

import bd.edu.seu.gamesclub.entity.EmailBroadcastRecipient;
import bd.edu.seu.gamesclub.entity.enums.DeliveryStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for per-recipient {@link EmailBroadcastRecipient} delivery logs.
 */
@Repository
public interface EmailBroadcastRecipientRepository extends JpaRepository<EmailBroadcastRecipient, Long> {

    /** All recipient records for a broadcast. */
    List<EmailBroadcastRecipient> findByBroadcastId(Long broadcastId);

    /** Count of recipients in a broadcast with a given delivery outcome. */
    long countByBroadcastIdAndDeliveryStatus(Long broadcastId, DeliveryStatus deliveryStatus);
}
