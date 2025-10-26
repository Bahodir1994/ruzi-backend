package app.ruzi.service.payload.app;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddCustomerReferrerToCartDto(
        @NotBlank String id,
        @NotBlank String cardSessionId,
        @NotBlank String type //CUSTOMER / REFERRER
) {
}

