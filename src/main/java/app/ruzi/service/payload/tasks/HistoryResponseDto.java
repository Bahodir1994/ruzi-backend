package app.ruzi.service.payload.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistoryResponseDto {
    private String parentId;
    private String status;
    private String insUserName;
    private String comment;
    private String insTime;
}