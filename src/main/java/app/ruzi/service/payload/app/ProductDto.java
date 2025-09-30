package app.ruzi.service.payload.app;

import app.ruzi.entity.app.Item;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link Item}
 */
public record ProductDto(
        Long id,
        String skuCode,
        String barcode,
        String name,
        String brand,
        String category,
        String unit,
        BigDecimal defaultSalePrice,
        String description,
        Boolean isActive
) implements Serializable {}