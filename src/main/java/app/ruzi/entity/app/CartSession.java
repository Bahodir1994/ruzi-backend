package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cart_sessions", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLDelete(sql = "UPDATE ruzi.cart_sessions SET is_deleted = true WHERE id = ?")
public class CartSession extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Qaysi klientga tegishli (multi-tenant)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Agar ushbu savdoni ma’lum bir usta olib kelgan bo‘lsa
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id")
    private Referrer referrer;

    /** Ixtiyoriy mijoz ma’lumotlari */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "cart_number", length = 20, unique = true, nullable = false)
    private String cartNumber;

    /** tarkibiy: CASH/CARD/MIXED */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentType paymentType = PaymentType.CASH;

    /** To‘lov holati: UNPAID / PARTIAL / PAID */
    @Enumerated(EnumType.STRING)
    @Column(length = 20
//            , nullable = false
    )
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    /** Qarz summasi (totalAmount - paidAmount), servisda yangilanadi */
    @Column(precision = 18, scale = 2)
    private BigDecimal debtAmount = BigDecimal.ZERO;

    /**
     * Kassir foydalanuvchi (Keycloak foydalanuvchisi)
     */
    @Column(name = "created_by_user", length = 100, nullable = false)
    private String createdByUser;

    /**
     * Qaysi omborda savdo bo‘layapti
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    /**
     * Savat holati: OPEN, CHECKED_OUT, CANCELLED
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Status status = Status.OPEN;

    /**
     * Savdo sanasi
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime closedAt;

    /**
     * Umumiy summa va to‘lov ma’lumotlari
     */
    @Column(precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * Savatdagi mahsulotlar
     */
    @OneToMany(mappedBy = "cartSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CartItem> items;

    public enum Status {OPEN, CHECKED_OUT, CANCELLED}
    public enum PaymentType {CASH, CARD, MIXED}
    public enum PaymentStatus {UNPAID, PARTIAL, PAID}

    // --- DEFAULT qiymatlar uchun PrePersist ---
    @PrePersist
    protected void onPrePersist() {
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.UNPAID;
        }
        if (paymentType == null) {
            paymentType = PaymentType.CASH;
        }
        if (status == null) {
            status = Status.OPEN;
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }
        if (debtAmount == null) {
            debtAmount = BigDecimal.ZERO;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
