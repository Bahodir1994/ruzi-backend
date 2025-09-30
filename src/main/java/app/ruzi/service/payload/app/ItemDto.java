package app.ruzi.service.payload.app;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemDto {
    private String id;
    private String name;
    private String sku;
    private Double price;
    private String primaryImageUrl;
}
