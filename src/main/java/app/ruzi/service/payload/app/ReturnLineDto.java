package app.ruzi.service.payload.app;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReturnLineDto {
    private String cartItemId;
    private Integer quantity;
}
