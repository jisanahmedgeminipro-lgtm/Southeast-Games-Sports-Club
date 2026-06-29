package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when an OTP is missing, incorrect, or has already been used.
 */
public class InvalidOtpException extends BusinessException {

    public InvalidOtpException(String message) {
        super(message);
    }
}
