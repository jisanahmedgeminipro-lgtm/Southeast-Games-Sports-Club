package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when business-level validation fails (e.g. passwords do not match),
 * distinct from bean-level {@code jakarta.validation} errors (maps to HTTP 400).
 */
public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message);
    }
}
