package bd.edu.seu.gamesclub.entity.enums;

/**
 * Lifecycle status of a membership drive ({@code membership_periods.status}).
 * Stored as {@code STRING}.
 */
public enum MembershipStatus {

    /** Created but not yet opened to students. */
    DRAFT,

    /** Currently accepting applications. */
    OPEN,

    /** No longer accepting applications. */
    CLOSED
}
