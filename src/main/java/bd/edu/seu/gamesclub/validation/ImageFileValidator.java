package bd.edu.seu.gamesclub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

/** Implements {@link ValidImage}. */
public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private long maxBytes;

    @Override
    public void initialize(ValidImage annotation) {
        this.maxBytes = annotation.maxBytes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // optional upload
        }
        String contentType = file.getContentType();
        boolean isImage = contentType != null && contentType.startsWith("image/");
        return isImage && file.getSize() <= maxBytes;
    }
}
