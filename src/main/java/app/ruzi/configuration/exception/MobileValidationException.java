package app.ruzi.configuration.exception;

import app.ruzi.configuration.validation.ValidationErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MobileValidationException extends RuntimeException {

    private final String resultcode;
    private final String errorCode;
    private final HttpStatus httpStatus;
    private final ValidationErrorResponse validationErrorResponse;

    public MobileValidationException(String resultcode, String errorCode, HttpStatus httpStatus) {
        super("Validation failed");
        this.resultcode = resultcode;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.validationErrorResponse = null;
    }

    public MobileValidationException(String resultcode,
                                     String errorCode,
                                     HttpStatus httpStatus,
                                     ValidationErrorResponse validationErrorResponse) {
        super("Validation failed");
        this.resultcode = resultcode;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.validationErrorResponse = validationErrorResponse;
    }
}
