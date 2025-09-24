package app.ruzi.service.payload.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RevisionHistoryDto {
    private Long rev;
    private LocalDateTime revDate;
    private String status;
    private List<FieldValueDto> fields;
    private String revisionType;
}
