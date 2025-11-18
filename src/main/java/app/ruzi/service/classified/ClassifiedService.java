package app.ruzi.service.classified;

import app.ruzi.service.enums.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ClassifiedService {

    public ClassifiedErrorsDto readErrorByCode(String lang, String code) {

        ErrorCode error = Arrays.stream(ErrorCode.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown error code: " + code));

        return switch (lang == null ? "uz" : lang.toLowerCase()) {
            case "en" -> new ClassifiedErrorsDto(error.getCode(), error.getEn());
            case "ru" -> new ClassifiedErrorsDto(error.getCode(), error.getRu());
            default -> new ClassifiedErrorsDto(error.getCode(), error.getUz());
        };
    }
}

