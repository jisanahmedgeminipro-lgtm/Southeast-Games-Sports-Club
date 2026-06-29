package bd.edu.seu.gamesclub.entity.enums;

/**
 * Application security roles. Stored as {@code STRING} in the {@code users.role}
 * column and consumed directly by Spring Security (hence the {@code ROLE_} prefix).
 */
public enum Role {

    /** Administrator. Accounts are created manually in the database. */
    ROLE_ADMIN,

    /** Registered student. Self-registers with an official {@code @seu.edu.bd} email. */
    ROLE_STUDENT
}
