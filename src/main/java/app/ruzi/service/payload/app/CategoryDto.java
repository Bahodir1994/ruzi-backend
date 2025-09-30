package app.ruzi.service.payload.app;

import app.ruzi.entity.app.Category;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link app.ruzi.entity.app.Category}
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryDto implements Serializable {
    String id;
    String code;
    String parentId;
    Timestamp insTime;
    @Builder.Default
    private List<CategoryDto> children = new ArrayList<>();
}