package app.ruzi.configuration.annotation.sizetext;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = Utf8SizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Utf8Size {
    String message() default "{validation.text.size}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int maxBytes();
}
