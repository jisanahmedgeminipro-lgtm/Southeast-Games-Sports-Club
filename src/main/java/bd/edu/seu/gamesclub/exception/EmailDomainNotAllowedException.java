package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when a registration is attempted with an email outside the allowed
 * university domain ({@code @seu.edu.bd}).
 */
public class EmailDomainNotAllowedException extends BusinessException {

    public EmailDomainNotAllowedException(String message) {
        super(message);
    }
}
