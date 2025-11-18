package app.ruzi.configuration.validation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse {
    private final List<FieldErrorInfo> errors = new ArrayList<>();

    public ValidationErrorResponse() {
    }

    public void addError(FieldErrorInfo error) {
        this.errors.add(error);
    }

}
