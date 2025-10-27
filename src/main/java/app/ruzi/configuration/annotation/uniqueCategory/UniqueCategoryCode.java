package app.ruzi.configuration.annotation.uniqueCategory;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueCategoryCodeValidator.class)
@Target({ElementType.TYPE}) // Butun DTOga qo'llanadi
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCategoryCode {
    String message() default "Bu kodli kategoriya allaqachon mavjud!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
