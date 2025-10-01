package app.ruzi.service.payload.app;

import app.ruzi.entity.app.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link Item}
 */
@AllArgsConstructor
@Getter
public class ProductRequestDto implements Serializable {
    private final Long id;
    private final String skuCode;
    private final String barcode;
    private final String name;
    private final String brand;
    private final String category;
    private final String unit;
    private final String description;
    private final Boolean isActive;
}