package app.ruzi.configuration.annotation.notfoundcolumn;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotFoundIdValidator.class)
public @interface NotFoundId {
    String message() default "{customValid.isPresent.value.message}";

    Class<?> entityName();

    String searchColumnName();

    boolean nullable() default false;  // Add the nullable attribute

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] defaultValue() default {"0"};
}



