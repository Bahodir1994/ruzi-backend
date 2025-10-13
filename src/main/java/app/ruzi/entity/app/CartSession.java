package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column(name = "cart_number", length = 20, unique = true, nullable = false)
    private String cartNumber;


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
     * Ixtiyoriy mijoz ma’lumotlari
     */
    private String customerName;
    private Long customerId;

    /**
     * Umumiy summa va to‘lov ma’lumotlari
     */
    @Column(precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentType paymentType = PaymentType.CASH;

    /**
     * Savatdagi mahsulotlar
     */
    @OneToMany(mappedBy = "cartSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CartItem> items;

    public enum Status {OPEN, CHECKED_OUT, CANCELLED}

    public enum PaymentType {CASH, CARD, MIXED}
}
