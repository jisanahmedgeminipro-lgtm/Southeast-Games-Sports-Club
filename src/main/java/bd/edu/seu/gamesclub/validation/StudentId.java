package bd.edu.seu.gamesclub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates a university student id: a numeric string of 6-20 digits.
 * Null/blank values pass (combine with {@code @NotBlank}).
 */
@Documented
@Constraint(validatedBy = StudentIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface StudentId {
    String message() default "Student ID must be a numeric value (6-20 digits)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
