package app.ruzi.service.payload.app;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RemoveCustomerReferrerToCartDto(
        @NotBlank String cartSessionId,
        @NotBlank String type //CUSTOMER / REFERRER
) {
}

