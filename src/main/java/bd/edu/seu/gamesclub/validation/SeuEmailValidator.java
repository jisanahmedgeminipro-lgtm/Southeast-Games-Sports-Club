package bd.edu.seu.gamesclub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;

/**
 * Implements {@link SeuEmail}. The allowed domain is injected from configuration
 * so it can be changed without recompiling.
 */
public class SeuEmailValidator implements ConstraintValidator<SeuEmail, String> {

    private final String allowedDomain;

    public SeuEmailValidator(@Value("${app.registration.allowed-email-domain:seu.edu.bd}") String allowedDomain) {
        this.allowedDomain = allowedDomain.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // let @NotBlank handle emptiness
        }
        return value.trim().toLowerCase(Locale.ENGLISH).endsWith("@" + allowedDomain);
    }
}
