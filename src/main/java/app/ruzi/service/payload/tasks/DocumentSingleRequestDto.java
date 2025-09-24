package app.ruzi.service.payload.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DocumentSingleRequestDto {
    private String parentId;

    private String id;

    private List<String> idList;

    private String type;
}
