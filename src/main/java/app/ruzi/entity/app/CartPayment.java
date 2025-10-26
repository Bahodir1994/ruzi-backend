package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_payment", schema = "ruzi",
        indexes = {
                @Index(name = "idx_cart_payment_cart", columnList = "cart_id"),
                @Index(name = "idx_cart_payment_client", columnList = "client_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartPayment extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Multi-tenant
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Qaysi chek bo‘yicha to‘lov
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartSession cartSession;

    /**
     * Mijoz (qarz to‘lovlarida qulay)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Method method; // CASH yoki CARD

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Terminal/kvitinga havola (ixtiyoriy)
     */
    @Column(length = 100)
    private String externalTxnId;

    /**
     * Kiritilgan vaqt (default CURRENT_TIMESTAMP ham bo‘lishi mumkin)
     */
    private LocalDateTime paidAt = LocalDateTime.now();

    public enum Method {CASH, CARD}
}
