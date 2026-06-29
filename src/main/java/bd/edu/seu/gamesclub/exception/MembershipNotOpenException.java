package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when a student attempts to apply while membership is not currently open.
 */
public class MembershipNotOpenException extends BusinessException {

    public MembershipNotOpenException(String message) {
        super(message);
    }
}
