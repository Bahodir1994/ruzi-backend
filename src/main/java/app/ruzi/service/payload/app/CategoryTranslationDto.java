package app.ruzi.service.payload.app;

import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link app.ruzi.entity.app.CategoryTranslation}
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryTranslationDto implements Serializable {
    String id;
    String categoryId;
    String languageCode;
    String name;
    String description;
}