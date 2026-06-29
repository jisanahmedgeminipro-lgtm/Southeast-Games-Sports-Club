package bd.edu.seu.gamesclub.exception;

/**
 * Thrown when a requested entity cannot be found (maps to HTTP 404).
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Convenience constructor producing a message like
     * {@code "Sport not found with id: 5"}.
     *
     * @param resource the resource type name (e.g. {@code "Sport"})
     * @param field    the lookup field (e.g. {@code "id"})
     * @param value    the value that was searched for
     */
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(resource + " not found with " + field + ": " + value);
    }
}
