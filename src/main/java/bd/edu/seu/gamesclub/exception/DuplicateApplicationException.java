package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when a student tries to apply more than once within the same
 * membership period.
 */
public class DuplicateApplicationException extends BusinessException {

    public DuplicateApplicationException(String message) {
        super(message);
    }
}
