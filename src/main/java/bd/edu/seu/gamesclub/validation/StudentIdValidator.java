package bd.edu.seu.gamesclub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/** Implements {@link StudentId}. */
public class StudentIdValidator implements ConstraintValidator<StudentId, String> {

    private static final Pattern PATTERN = Pattern.compile("\\d{6,20}");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return PATTERN.matcher(value.trim()).matches();
    }
}
