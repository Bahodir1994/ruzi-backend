package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "item",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "code"}),
                @UniqueConstraint(columnNames = {"client_id", "sku_code"})
        },
        indexes = {
                @Index(name = "idx_item_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Column(name = "name", length = 600, nullable = false)
    private String name;

    @Column(name = "price")
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "primary_image_url", length = 300)
    private String primaryImageUrl;

    @Column(name = "internal_sku_number")
    private Integer internalSkuNumber;

    @Column(nullable = false, length = 50)
    private String skuCode;

    @Column(unique = true, length = 100)
    private String barcode;

    @Column(length = 200)
    private String brand;

    @Column(length = 3)
    private String unit;

    @Column(length = 600)
    private String description;

}


