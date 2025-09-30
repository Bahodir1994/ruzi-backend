package app.ruzi.service.payload.app;

import app.ruzi.entity.app.Category;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Category}
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryWithItemDto implements Serializable {
    private String id;
    private String code;
    private String primaryImageUrl;
    private List<ItemDto> items;
}