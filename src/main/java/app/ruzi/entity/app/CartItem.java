package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "cart_item",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"cart_id", "purchase_order_item_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CartItem extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Qaysi cart sessiyaga tegishli
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference
    private CartSession cartSession;

    /**
     * Qaysi partiyadan chiqyapti (narx va batch shu yerdan olinadi)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_item_id", nullable = false)
    private PurchaseOrderItem purchaseOrderItem;

    /**
     * Qaysi ombor zaxirasidan chiqyapti
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    /**
     * Sotilgan miqdor (kg, litr, dona ...)
     */
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal quantity = BigDecimal.ZERO;

    /**
     * Sotuv narxi (partiya narxidan olinadi)
     */
    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Chegirma (ixtiyoriy)
     */
    @Column(precision = 18, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    /**
     * Yakuniy summa (quantity × price − discount)
     */
    @Column(name = "line_total", precision = 18, scale = 2)
    private BigDecimal lineTotal;
}
