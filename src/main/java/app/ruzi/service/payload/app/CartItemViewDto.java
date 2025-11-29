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
        BigDecimal availableAlt,
        BigDecimal conversionRate,
        String warehouseName,

        // --- 🔽 PurchaseOrderItem'dan ---
        String unitCode,
        Integer packageCount,        // Asl sotuv narxi
        BigDecimal salePrice,        // Asl sotuv narxi
        BigDecimal altSalePrice,        // Asl sotuv narxi birlikda
        BigDecimal minimalSum,       // Minimal sotuv narxi
        BigDecimal purchasePrice,    // Xarid narxi
        BigDecimal purchaseDiscount, // Xarid paytidagi chegirma (supplier)

        // --- 🔽 CartItem'dan ---
        BigDecimal saleDiscount     // Kassir bergan chegirma (bizning)
) {
}

