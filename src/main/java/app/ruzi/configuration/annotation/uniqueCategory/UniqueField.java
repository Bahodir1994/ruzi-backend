package app.ruzi.configuration.annotation.uniqueCategory;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueFieldValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UniqueFields.class) // 👈 birdan ortiq ishlatish uchun
public @interface UniqueField {

    /**
     * Qaysi Entity klassda tekshiriladi.
     * Masalan: Category.class yoki Item.class
     */
    Class<?> entityClass();

    /**
     * Entity ichida qaysi maydon(lar) bo‘yicha tekshiriladi.
     * Masalan: {"code"} yoki {"name", "code"}
     */
    String[] fieldEntity();

    /**
     * Validatsiya xabari (default bo‘lishi mumkin).
     */
    String message() default "Ushbu qiymat bilan yozuv allaqachon mavjud!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
