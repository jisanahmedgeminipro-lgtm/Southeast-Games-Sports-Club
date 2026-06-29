package bd.edu.seu.gamesclub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that an email belongs to the official university domain
 * (configured by {@code app.registration.allowed-email-domain}, default
 * {@code seu.edu.bd}). Null/blank values pass (combine with {@code @NotBlank}).
 */
@Documented
@Constraint(validatedBy = SeuEmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface SeuEmail {
    String message() default "Only official @seu.edu.bd email addresses are allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
