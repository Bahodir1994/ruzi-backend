package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "item", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String skuCode;

    @Column(unique = true, length = 100)
    private String barcode;

    @Column(nullable = false)
    private String name;

    private String brand;

    private String category;

    private String unit;

    private BigDecimal defaultSalePrice;

    private String description;

    private Boolean isActive = true;
}

