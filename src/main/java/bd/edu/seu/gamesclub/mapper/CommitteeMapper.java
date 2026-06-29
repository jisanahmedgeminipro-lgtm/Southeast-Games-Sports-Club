package bd.edu.seu.gamesclub.mapper;

import bd.edu.seu.gamesclub.dto.CommitteeMemberRequest;
import bd.edu.seu.gamesclub.dto.CommitteeMemberResponse;
import bd.edu.seu.gamesclub.entity.CommitteeMember;
import bd.edu.seu.gamesclub.entity.enums.CommitteeType;
import bd.edu.seu.gamesclub.util.MediaUrls;

/** Manual mapper for {@link CommitteeMember}. Photo relation is resolved by the service. */
public final class CommitteeMapper {

    private CommitteeMapper() {
    }

    public static CommitteeMemberResponse toResponse(CommitteeMember m) {
        if (m == null) {
            return null;
        }
        return new CommitteeMemberResponse(
                m.getId(),
                m.getCommitteeType() != null ? m.getCommitteeType().name() : null,
                m.getName(),
                m.getDepartment(),
                m.getBatch(),
                m.getPosition(),
                MediaUrls.url(m.getPhoto()),
                m.getFacebookUrl(),
                m.getLinkedinUrl(),
                m.getSessionYear(),
                m.getDisplayOrder(),
                m.isActive(),
                m.getPhoto() != null ? m.getPhoto().getId() : null
        );
    }

    public static void apply(CommitteeMember m, CommitteeMemberRequest r) {
        m.setCommitteeType(CommitteeType.valueOf(r.committeeType()));
        m.setName(r.name());
        m.setDepartment(r.department());
        m.setBatch(r.batch());
        m.setPosition(r.position());
        m.setFacebookUrl(r.facebookUrl());
        m.setLinkedinUrl(r.linkedinUrl());
        m.setSessionYear(r.sessionYear());
        if (r.displayOrder() != null) {
            m.setDisplayOrder(r.displayOrder());
        }
        if (r.active() != null) {
            m.setActive(r.active());
        }
    }
}
