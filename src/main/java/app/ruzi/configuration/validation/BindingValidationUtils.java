package app.ruzi.configuration.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Locale;
import java.util.Optional;

@Component
public class BindingValidationUtils {

    private final MessageSource messageSource;

    public BindingValidationUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Optional<ValidationErrorResponse> buildErrorResponse(BindingResult bindingResult, String langCode) {
        if (bindingResult.hasErrors()) {
            Locale currentLocale = getCurrentLocale(langCode);
            ValidationErrorResponse response = new ValidationErrorResponse();

            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError) {
                    String field = ((FieldError) error).getField();
                    String localizedErrorMessage = getLocalizedErrorMessage(error, currentLocale);
                    FieldErrorInfo fieldErrorInfo = new FieldErrorInfo(field, localizedErrorMessage);
                    response.addError(fieldErrorInfo);
                }
            }

            return Optional.of(response);
        }
        return Optional.empty(); // If no errors are present, return an empty Optional
    }

    public Locale getCurrentLocale(String langCode) {
        if (langCode != null) {
            return new Locale(langCode);
        }
        return LocaleContextHolder.getLocale();
    }

    public String getLocalizedErrorMessage(ObjectError error, Locale locale) {
        String localizedMessage = messageSource.getMessage(error, locale);
        if (!localizedMessage.isEmpty()) {
            return localizedMessage;
        }
        return error.getDefaultMessage();
    }
}

