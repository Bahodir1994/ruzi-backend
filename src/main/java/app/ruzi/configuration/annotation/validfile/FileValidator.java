package app.ruzi.configuration.annotation.validfile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Autowired
    private MessageSource messageSource; // Inject MessageSource for i18n support

    private long maxSizeInBytes;
    private long minSizeInBytes;
    private double maxSizeInMB;
    private double minSizeInMB;
    private List<String> allowedFormats;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.maxSizeInMB = constraintAnnotation.maxSize();
        this.minSizeInMB = constraintAnnotation.minSize();
        this.maxSizeInBytes = (long) (maxSizeInMB * 1024 * 1024);
        this.minSizeInBytes = (long) (minSizeInMB * 1024 * 1024);
        this.allowedFormats = Arrays.asList(constraintAnnotation.format());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        long fileSize = file.getSize();
        String fileExtension = getFileExtension(file.getOriginalFilename());

        if (fileSize > maxSizeInBytes || fileSize < minSizeInBytes || !allowedFormats.contains(fileExtension)) {
            // ✅ Fetch message from `messages.properties` dynamically
            String messageTemplate = messageSource.getMessage(
                    "validation.file",
                    new Object[]{minSizeInMB, maxSizeInMB, String.join(", ", allowedFormats)},
                    LocaleContextHolder.getLocale() // Use user's locale (Uzbek, English, etc.)
            );

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation();

            return false;
        }

        return true;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }
}
