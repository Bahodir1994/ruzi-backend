package app.ruzi.service.payload.app;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItemViewDto(
        String cartItemId,
        String purchaseOrderItemId,
        String itemName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        BigDecimal available,
        String warehouseName
) {}

