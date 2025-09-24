package app.ruzi.configuration.annotation.validfile;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {

    String message() default "{validation.file}"; // Use message key from properties

    double maxSize() default 10.0; // Default max size is 10MB

    double minSize() default 0.0;  // Default min size is 0MB (optional)

    String[] format() default {"pdf", "xlsx", "jpeg"}; // Allowed file types

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


