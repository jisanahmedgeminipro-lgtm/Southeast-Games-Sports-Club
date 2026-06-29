package bd.edu.seu.gamesclub.entity.enums;

/** Overall dispatch status of an email broadcast. Stored as {@code STRING}. */
public enum BroadcastStatus {

    /** Queued, not yet processed. */
    PENDING,

    /** Delivered to all recipients successfully. */
    SENT,

    /** Delivered to some recipients; others failed. */
    PARTIAL,

    /** Delivery failed for all recipients. */
    FAILED
}
