package app.ruzi.service.payload.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FieldValueDto {
    private String fieldName;
    private Object fieldValue;
}

