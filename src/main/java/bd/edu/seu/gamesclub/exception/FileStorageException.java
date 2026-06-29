package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when an uploaded file cannot be stored or is otherwise invalid.
 */
public class FileStorageException extends BusinessException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
