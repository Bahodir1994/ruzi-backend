package app.ruzi.configuration.annotation.uniqueCategory;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UniqueFields {
    UniqueField[] value();
}
