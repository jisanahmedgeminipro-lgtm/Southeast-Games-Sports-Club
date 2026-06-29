package bd.edu.seu.gamesclub.entity.enums;

/**
 * Audience selector for an admin email broadcast
 * ({@code email_broadcasts.target_type}). Stored as {@code STRING}.
 */
public enum BroadcastTarget {

    /** Every registered student. */
    ALL_STUDENTS,

    /** Only approved members of the active membership period. */
    MEMBERS_ONLY,

    /** A manually selected subset of students. */
    SELECTED
}
