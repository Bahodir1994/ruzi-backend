package app.ruzi.entity.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PurchaseOrderItem (Xarid buyurtmasidagi mahsulot/xizmat).
 * <p>
 * Har bir xarid buyurtmasida bir nechta mahsulot yoki xizmat bo‘lishi mumkin.
 * Ushbu jadval shu buyurtmaga tegishli barcha itemlarni saqlaydi.
 * <p>
 * Multi-tenant qoida: har bir yozuv ma’lum bir Client ga tegishli.
 */
@Entity
@Table(
        name = "purchase_order_item",
        schema = "ruzi",
        indexes = {
                @Index(name = "idx_purchase_order_item_client_id", columnList = "client_id"),
                @Index(name = "idx_purchase_order_item_order_id", columnList = "purchase_order_id"),
                @Index(name = "idx_purchase_order_item_item_id", columnList = "item_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchaseOrderItem {

    /**
     * Primary Key (avtomatik ID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    /**
     * 🔑 Har bir yozuv qaysi Client’ga tegishli ekanini ko‘rsatadi.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Bog‘lanish: Qaysi xarid buyurtmasiga tegishli.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    /**
     * Bog‘lanish: Qaysi mahsulot/xizmat kiritilgan.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * Jismoniy birliklar soni (qop, dona, idish va h.k.)
     */
    @Column(name = "package_count", nullable = false)
    private Integer packageCount = 0;

    /**
     * Umumiy og‘irlik yoki hajm (kg, litr, metr)
     */
    @Column(name = "quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(precision = 10, scale = 3)
    private BigDecimal conversionRate;

    /**
     * O‘lchov birligi kodi (masalan: pcs, kg, l).
     */
    @Column(name = "unit_code", length = 10, nullable = false)
    private String unitCode;

    @Column(name = "alt_unit_code", length = 10
//            , nullable = false
    )
    private String altUnitCode;

    /**
     * Xarid narxi (bir dona uchun, masalan: 50 000.00 so‘m).
     */
    @Column(name = "purchase_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    /**
     * Sotuv narxi (agar mavjud bo‘lsa, masalan: 60 000.00 so‘m).
     */
    @Column(name = "sale_price", precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "alt_sale_price", precision = 15, scale = 2)
    private BigDecimal altSalePrice;

    /**
     * Umumiy summa (quantity × purchasePrice).
     */
    @Column(name = "sum", precision = 15, scale = 2, nullable = false)
    private BigDecimal sum = BigDecimal.ZERO;

    /**
     * Minimal summa (biznes qoidasi bo‘yicha talab qilinadigan minimal qiymat).
     */
    @Column(name = "minimal_sum", precision = 15, scale = 2)
    private BigDecimal minimalSum;

    /**
     * Chegirma summasi (masalan: 50 000 so‘m).
     */
    @Column(name = "discount", precision = 15, scale = 2)
    private BigDecimal discount;

    /**
     * Partiya yoki seriya raqami (masalan: BATCH-2025-01).
     */
    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    /**
     * Yaroqlilik muddati (masalan: 2025-12-31).
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    public PurchaseOrderItem(String purchaseOrderItemId) {
        this.id = purchaseOrderItemId;
    }

    @PrePersist
    protected void onPrePersist() {
        if (conversionRate == null) {
            conversionRate = BigDecimal.ONE;
        }
    }
}

