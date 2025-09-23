package app.ruzi.configuration.messaging;

import app.ruzi.configuration.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
public class HandlerService {
    private final ValidationService validationService;

    /**
     * Generalized method to handle request processing and validation.
     *
     * @param supplier      Data supplier function for processing request.
     * @param bindingResult Binding result for validation.
     * @param langType      Language type for validation messages.
     * @return MessageResponse with success or error details.
     */
    public MessageResponse handleRequest(
            Supplier<Object> supplier,
            BindingResult bindingResult,
            String langType
    ) {
        return validationService.method_v1(bindingResult, langType)
                .map(validationErrorResponse -> new MessageResponse("error", false, validationErrorResponse, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> new MessageResponse("success", true, supplier.get(), HttpStatus.OK));
    }

    /**
     * For void methods
     */
    public MessageResponse handleRequest(
            Runnable action,
            BindingResult bindingResult,
            String langType
    ) {
        return validationService.method_v1(bindingResult, langType)
                .map(validationErrorResponse ->
                        new MessageResponse("error", false, validationErrorResponse, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> {
                    action.run();
                    return new MessageResponse("success", true, null, HttpStatus.OK);
                });
    }


}
