package app.ruzi.service.payload.app;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateCartItemPriceDto(
        @NotBlank String cartItemId,
        @NotNull BigDecimal newPrice,
        BigDecimal discountPercent
) {}
