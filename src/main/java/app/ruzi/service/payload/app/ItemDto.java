package app.ruzi.service.payload.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link app.ruzi.entity.app.Item}
 */
@AllArgsConstructor
@Getter
@Setter
public class ItemDto implements Serializable {
    private final String insUser;
    private final String updUser;
    private final Timestamp insTime;
    private final Timestamp updTime;
    private final Boolean isDeleted;
    private String id;
    private final String code;
    private final String name;
    private final Double price;
    private final Boolean isActive;
    private final String primaryImageUrl;
    private final String skuCode;
    private final String barcode;
    private final String brand;
    private final String unit;
    private final String description;
}