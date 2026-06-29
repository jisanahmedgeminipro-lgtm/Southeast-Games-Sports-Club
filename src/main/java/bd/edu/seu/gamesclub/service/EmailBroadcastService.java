package bd.edu.seu.gamesclub.service;

import bd.edu.seu.gamesclub.dto.BroadcastRequest;
import bd.edu.seu.gamesclub.dto.EmailBroadcastResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Admin email broadcasts to all students, members only, or a selected subset,
 * with persisted campaign history.
 */
public interface EmailBroadcastService {

    /**
     * Resolve recipients for the chosen target, persist the campaign + per-recipient
     * log, and dispatch the emails asynchronously.
     *
     * @param request   subject/body/target (+ ids when SELECTED)
     * @param adminEmail the sending admin (recorded as the creator)
     * @return the persisted broadcast summary
     */
    EmailBroadcastResponse send(BroadcastRequest request, String adminEmail);

    /** Paginated broadcast history, newest first. */
    Page<EmailBroadcastResponse> history(Pageable pageable);
}
