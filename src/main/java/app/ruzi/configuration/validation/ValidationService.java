package app.ruzi.configuration.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
public class ValidationService implements ValidationServiceImpl {
    private final BindingValidationUtils bindingValidationUtils;

    public Optional<ValidationErrorResponse> method_v1(BindingResult bindingResult, String langType) {
        return this.bindingValidationUtils.buildErrorResponse(bindingResult, langType);
    }

    public Optional<ValidationErrorResponse> method_v2(BindingResult bindingResult, String langType) {
        return Optional.empty();
    }

    public ValidationService(BindingValidationUtils bindingValidationUtils) {
        this.bindingValidationUtils = bindingValidationUtils;
    }
}
