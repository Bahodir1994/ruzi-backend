package app.ruzi.service.payload.app;

public record CreatePurchaseOrderItemDto(
        String orderId,
        String itemId
) {
}

