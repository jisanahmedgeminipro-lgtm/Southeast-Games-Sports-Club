package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when a uniqueness constraint would be violated - e.g. duplicate email,
 * student id, sport name or hero-slider display order (maps to HTTP 409).
 */
public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
