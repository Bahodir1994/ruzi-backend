package app.ruzi.configuration.annotation.uniqueCategory;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.entity.app.Category;
import app.ruzi.repository.app.CategoryRepository;
import app.ruzi.service.payload.app.CategoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
@RequiredArgsConstructor
public class UniqueCategoryCodeValidator implements ConstraintValidator<UniqueCategoryCode, CategoryDto> {
    private final JwtUtils jwtUtils;
    private final CategoryRepository categoryRepository;

    @Override
    public boolean isValid(CategoryDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getCode() == null) {
            return true; // boshqa validatorlar tekshiradi
        }
        UserJwt userJwt = new UserJwt();
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        Category existing = categoryRepository.findByQueryUniCat(userJwt.getClientId(), dto.getCode()).orElse(null);

        if (existing == null) return true;

        if (dto.getId() != null && existing.getId().equals(dto.getId())) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("{validation.unique.messages}")
                .addPropertyNode("code")
                .addConstraintViolation();
        return false;
    }
}

