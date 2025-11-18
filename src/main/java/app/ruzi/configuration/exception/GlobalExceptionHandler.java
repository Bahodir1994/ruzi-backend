package app.ruzi.configuration.exception;

import app.ruzi.service.classified.ClassifiedErrorsDto;
import app.ruzi.service.classified.ClassifiedService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ClassifiedService classifiedService;

    public GlobalExceptionHandler(ClassifiedService classifiedService) {
        this.classifiedService = classifiedService;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ErrorResponse {
        private String message;
        private Boolean success;
        private Object data;
        private HttpStatus status;

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        if ("Forbidden_annotation".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(
                            new ErrorResponse(
                                    "error",
                                    false,
                                    HttpEntity.EMPTY,
                                    HttpStatus.FORBIDDEN
                            )
                    );
        }
        return null;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        String langType = request.getHeader("Accept-Language");

        String errorCode = ex.getReason();

        final ClassifiedErrorsDto classifiedErrorsDto = classifiedService.readErrorByCode(langType, errorCode);

        return ResponseEntity.status(ex.getStatusCode())
                .body(
                        new ErrorResponse(
                                classifiedErrorsDto.getName(),
                                false,
                                HttpEntity.EMPTY,
                                HttpStatus.valueOf(ex.getStatusCode().value())
                        ));
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<Object> handleCustomValidationException(
            CustomValidationException ex,
            HttpServletRequest request
    ) {
        String langType = request.getHeader("Accept-Language");
        final ClassifiedErrorsDto localized = classifiedService.readErrorByCode(langType, ex.getErrorCode());

        return ResponseEntity.status(ex.getHttpStatus())
                .body(
                        new ErrorResponse(
                                localized.getName(),
                                false,
                                ex.getErrors(),
                                ex.getHttpStatus()
                        )
                );
    }

    @ExceptionHandler(MobileValidationException.class)
    public ResponseEntity<Object> handleMobileValidationException(
            MobileValidationException ex,
            HttpServletRequest request
    ) {
        String langType = request.getHeader("Accept-Language");
        final ClassifiedErrorsDto localized = classifiedService.readErrorByCode(langType, ex.getErrorCode());

        Map<String, Object> map = new HashMap<>();
        map.put("resultcode", ex.getResultcode());
        map.put("resultnote", localized.getName());
        map.put("data", null);
        return ResponseEntity.ok() // mobil app talabiga kora xar qanday xolatda 200 kelsin (portlab ketsaxam 🤣)
                .body(map);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleOptimisticLock(
            HttpServletRequest request
    ) {
        String langType = request.getHeader("Accept-Language");

        String errorCode = "WARN0001";

        final ClassifiedErrorsDto classifiedErrorsDto = classifiedService.readErrorByCode(langType, errorCode);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                classifiedErrorsDto.getName(),
                                false,
                                HttpEntity.EMPTY,
                                HttpStatus.CONFLICT
                        ));
    }
}