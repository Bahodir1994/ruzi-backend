package app.ruzi.service.payload.app;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DeleteCartItemDto(
        @NotBlank String cartItemId
) {
}
