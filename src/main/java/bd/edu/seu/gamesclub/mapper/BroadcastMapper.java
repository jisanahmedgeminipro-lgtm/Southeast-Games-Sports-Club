package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.EmailBroadcastResponse;
import bd.edu.seu.gamesclub.entity.EmailBroadcast;

/** Manual mapper for {@link EmailBroadcast}. */
public final class BroadcastMapper {

    private BroadcastMapper() {
    }

    public static EmailBroadcastResponse toResponse(EmailBroadcast b) {
        if (b == null) {
            return null;
        }
        return new EmailBroadcastResponse(
                b.getId(),
                b.getSubject(),
                b.getTargetType() != null ? b.getTargetType().name() : null,
                b.getRecipientCount(),
                b.getStatus() != null ? b.getStatus().name() : null,
                b.getSentAt(),
                b.getCreatedAt()
        );
    }
}
