package app.ruzi.configuration.annotation.sizetext;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.charset.StandardCharsets;

public class Utf8SizeValidator implements ConstraintValidator<Utf8Size, String> {
    private int maxBytes;

    @Override
    public void initialize(Utf8Size constraintAnnotation) {
        this.maxBytes = constraintAnnotation.maxBytes();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.getBytes(StandardCharsets.UTF_8).length <= maxBytes;
    }
}
