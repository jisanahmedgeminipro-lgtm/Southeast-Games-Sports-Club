package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.ContactMessageResponse;
import bd.edu.seu.gamesclub.dto.ContactRequest;
import bd.edu.seu.gamesclub.entity.ContactMessage;

/** Manual mapper for {@link ContactMessage}. */
public final class ContactMapper {

    private ContactMapper() {
    }

    public static ContactMessage toEntity(ContactRequest r) {
        ContactMessage m = new ContactMessage();
        m.setName(r.name());
        m.setEmail(r.email());
        m.setSubject(r.subject());
        m.setMessage(r.message());
        return m;
    }

    public static ContactMessageResponse toResponse(ContactMessage m) {
        if (m == null) {
            return null;
        }
        return new ContactMessageResponse(
                m.getId(), m.getName(), m.getEmail(), m.getSubject(),
                m.getMessage(), m.isRead(), m.getCreatedAt()
        );
    }
}
