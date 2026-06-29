package bd.edu.seu.gamesclub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates an uploaded {@code MultipartFile} as an image within the size limit.
 * Empty/absent files pass (use for optional uploads; pair with a presence check
 * when the image is mandatory).
 */
@Documented
@Constraint(validatedBy = ImageFileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
    String message() default "File must be an image up to 5MB";
    long maxBytes() default 5L * 1024 * 1024;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
