package app.ruzi.service.payload.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link app.ruzi.entity.app.Category}
 */
@AllArgsConstructor
@Getter
@Setter
public class CategoryDto implements Serializable {
    private final String insUser;
    private final String updUser;
    private final Timestamp insTime;
    private final Timestamp updTime;
    private final Boolean isDeleted;
    private final String id;
    private final String code;
    private final String primaryImageUrl;
}