package app.ruzi.service.payload.app;

import app.ruzi.configuration.annotation.uniqueCategory.UniqueField;
import app.ruzi.entity.app.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link app.ruzi.entity.app.Category}
 */
@AllArgsConstructor
@Getter
@Setter
public class CategoryDto implements Serializable {

    private final String id;

    @UniqueField(entityClass = Category.class, fieldEntity = {"code"})
    private final String code;

    private final String primaryImageUrl;
    private final List<String> items;
}