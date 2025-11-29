package app.ruzi.service.payload.app;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddCartItemDto(
        @NotBlank String sessionId,
        @NotBlank String purchaseOrderItemId,

        // Asosiy birlikdagi miqdor (PACK)
        BigDecimal packQuantity,

        // Qo‘shimcha birlikdagi miqdor (ALT)
        BigDecimal altQuantity
) {
}

