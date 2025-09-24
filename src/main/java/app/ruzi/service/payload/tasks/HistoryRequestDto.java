package app.ruzi.service.payload.tasks;

import app.ruzi.configuration.annotation.notfoundcolumn.NotFoundId;
import app.ruzi.entity.tasks.History;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class HistoryRequestDto {
    @NotFoundId(entityName = History.class, searchColumnName = "parent_id", nullable = true)
    private String parentId;

    private List<String> status;

    public HistoryRequestDto(String parentId) {
        this.parentId = parentId;
    }

    public HistoryRequestDto(String parentId, List<String> status) {
        this.parentId = parentId;
        this.status = status;
    }
}