package bd.edu.seu.gamesclub.dto;

/** Read model for a committee member. */
public record CommitteeMemberResponse(
        Long id,
        String committeeType,
        String name,
        String department,
        String batch,
        String position,
        String photoUrl,
        String facebookUrl,
        String linkedinUrl,
        String sessionYear,
        int displayOrder,
        boolean active,
        Long photoMediaId
) {}
