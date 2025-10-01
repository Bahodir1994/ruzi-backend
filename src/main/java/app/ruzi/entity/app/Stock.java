package app.ruzi.entity;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import app.ruzi.entity.app.Client;
import app.ruzi.entity.app.PurchaseOrderItem;
import app.ruzi.entity.app.Warehouse;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    /**
     * savatlarga rezerv qilingan
     */
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    /**
     * minimal zaxira
     */
    @Column(name = "min_threshold")
    private Integer minThreshold = 0;

    /**
     * maksimal qoldiq
     */
    @Column(name = "max_threshold")
    private Integer maxThreshold;
}
