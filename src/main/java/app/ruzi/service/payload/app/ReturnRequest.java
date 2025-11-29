package app.ruzi.service.payload.app;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReturnRequest {
    private String cartSessionId;
    private String reason;

    private List<ReturnLineDto> items;
}


