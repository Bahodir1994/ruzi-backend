package app.ruzi.configuration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Getter
public class CustomValidationException extends RuntimeException {
    private final List<Map<String, Object>> errors;
    private final String errorCode;
    private final HttpStatus httpStatus;

    public CustomValidationException(List<Map<String, Object>> errors, String errorCode, HttpStatus httpStatus) {
        super("Validation failed");
        this.errors = errors;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}

