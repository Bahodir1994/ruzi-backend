package app.ruzi.configuration.validation;

import java.util.Optional;
import org.springframework.validation.BindingResult;

public interface ValidationServiceImpl {
    Optional<ValidationErrorResponse> method_v1(BindingResult var1, String var2);

    Optional<ValidationErrorResponse> method_v2(BindingResult var1, String var2);
}
