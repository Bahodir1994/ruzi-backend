package app.ruzi.configuration.validation;

import org.springframework.validation.BindingResult;

import java.util.Optional;

public interface ValidationServiceImpl {
    Optional<ValidationErrorResponse> method_v1(BindingResult var1, String var2);

    Optional<ValidationErrorResponse> method_v2(BindingResult var1, String var2);
}
