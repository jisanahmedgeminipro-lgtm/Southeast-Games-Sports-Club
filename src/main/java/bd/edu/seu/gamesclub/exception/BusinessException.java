package bd.edu.seu.gamesclub.exception;

/**
 * Base type for all domain/business rule violations in the application.
 *
 * <p>Service-layer code throws subclasses of this exception (never a raw
 * {@link RuntimeException}) so the (future) global exception handler can map each
 * case to an appropriate HTTP status and user-facing message.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
