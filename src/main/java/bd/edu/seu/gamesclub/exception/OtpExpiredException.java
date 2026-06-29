package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when an OTP has passed its 5-minute validity window.
 */
public class OtpExpiredException extends BusinessException {

    public OtpExpiredException(String message) {
        super(message);
    }
}
