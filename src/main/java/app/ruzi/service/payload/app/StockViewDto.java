package app.ruzi.service.payload.app;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockViewDto {
    private String stockId;

    private BigDecimal quantity;
    private BigDecimal reservedQuantity;
    private BigDecimal availableQuantity;

    private String warehouseId;
    private String warehouseName;
    private String warehouseCode;

    private String purchaseOrderItemId;
    private BigDecimal salePrice;
    private BigDecimal minimalSum;
    private BigDecimal purchasePrice;
    private String batchNumber;
    private LocalDate expiryDate;
    private BigDecimal discount;

    private String itemId;
    private String itemCode;
    private String itemName;
    private String barcode;
    private String unitName;
    private String categoryName;
    private String imageUrl;

    private String clientId;
}
