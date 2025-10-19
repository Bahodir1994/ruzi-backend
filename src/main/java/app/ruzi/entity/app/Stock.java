package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "stock",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"purchase_order_item_id", "warehouse_id"})
        },
        indexes = {
                @Index(name = "idx_stock_client", columnList = "client_id"),
                @Index(name = "idx_stock_item", columnList = "purchase_order_item_id"),
                @Index(name = "idx_stock_wh", columnList = "warehouse_id")
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * Qaysi klientga tegishli
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Partiya (PurchaseOrderItem) bilan bog‘lanadi
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_item_id", nullable = false)
    private PurchaseOrderItem purchaseOrderItem;

    /**
     * Qaysi ombor
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    /**
     * real qoldiq
     */
    @Column(name = "quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    /**
     * real qoldiq PCS da
     */
    @Column(name = "alt_quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal altQuantity = BigDecimal.ZERO;

    /**
     * Savatlarga rezerv qilingan miqdor.
     * Masalan, kassir savatga qo‘shgan, lekin hali to‘lov qilinmagan mahsulot miqdori.
     * Ombordagi real miqdordan ajratilgan, lekin hali chiqim qilinmagan.
     */
    @Column(name = "reserved_quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal reservedQuantity = BigDecimal.ZERO;

    @Column(name = "reserved_alt_quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal reservedAltQuantity = BigDecimal.ZERO;

    /**
     * Minimal zaxira (threshold) — ogohlantirish uchun.
     * Masalan, zaxira 5 kg dan past bo‘lsa, tizim signal beradi.
     */
    @Column(name = "min_threshold", precision = 18, scale = 3)
    private BigDecimal minThreshold = BigDecimal.ZERO;

    /**
     * Maksimal zaxira (optional).
     * Masalan, bu tovar uchun 100 kg dan ortiq saqlanmasin.
     */
    @Column(name = "max_threshold", precision = 18, scale = 3)
    private BigDecimal maxThreshold;


    @Transient
    public BigDecimal getAvailableQuantity() {
        BigDecimal q = (quantity != null) ? quantity : BigDecimal.ZERO;
        BigDecimal r = (reservedQuantity != null) ? reservedQuantity : BigDecimal.ZERO;
        return q.subtract(r);
    }
}
