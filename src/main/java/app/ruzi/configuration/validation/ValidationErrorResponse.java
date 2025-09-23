package app.ruzi.configuration.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {
    private final List<FieldErrorInfo> errors = new ArrayList();

    public ValidationErrorResponse() {
    }

    public void addError(FieldErrorInfo error) {
        this.errors.add(error);
    }

    public List<FieldErrorInfo> getErrors() {
        return this.errors;
    }
}
