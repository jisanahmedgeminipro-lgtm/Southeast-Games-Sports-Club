package bd.edu.seu.gamesclub.entity.enums;

/**
 * Discriminator distinguishing the two committees that share the
 * {@code committee_members} table. Stored as {@code STRING}.
 */
public enum CommitteeType {
    EXECUTIVE,
    SUB_EXECUTIVE
}
