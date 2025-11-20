package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PurchaseOrder (Xarid buyurtmasi) entitiy.
 * <p>
 * Bu jadval orqali tashkilotda xarid buyurtmalari boshqariladi.
 * Har bir xarid buyurtmasi ta'minotchi, ombor, yaratuvchi va
 * tasdiqlovchi xodim bilan bog‘lanadi hamda umumiy summasi,
 * to‘langan qismi va qarzi hisoblanadi.
 */
@Entity
@Table(
        name = "purchase_order",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "order_number"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PurchaseOrder extends AbstractAuditingEntity {

    /**
     * Primary Key (avtomatik generatsiya qilinadi).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    /**
     * Buyurtma raqami (№ hujjat).
     * Unikal bo‘lishi kerak. Masalan: PO-2025-001.
     * Buyurtma yaratilganda avtomatik yoki qo‘lda beriladi.
     */
    @Column(nullable = false, length = 50)
    private String orderNumber;

    /**
     * Ta’minotchi (Supplier) bilan bog‘liq.
     * Xarid kimdan amalga oshirilganini ko‘rsatadi.
     * Har doim to‘ldirilishi shart.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    /**
     * Ombor (Warehouse) bilan bog‘liq.
     * Xarid qaysi omborga kirim qilinishini ko‘rsatadi.
     * Har doim to‘ldirilishi shart.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    /**
     * Buyurtma yaratilgan sana.
     * Hujjat yaratilganda avtomatik yoziladi (system date).
     */
    private LocalDate createdAt;

    /**
     * Buyurtma tasdiqlangan sana.
     * Faqat manager/admin tomonidan APPROVED bo‘lganda yoziladi.
     */
    private LocalDate approvedAt;

    /**
     * Buyurtmani yaratgan xodim.
     * Xarid buyurtmasini kim kiritganini ko‘rsatadi.
     */
    @Column(name = "created_by_user_id", length = 50)
    private String createdByUserId;

    /**
     * Buyurtmani tasdiqlagan xodim.
     * Faqat APPROVED statusiga o‘tganda yoziladi.
     */
    @Column(name = "approved_by_user_id", length = 50)
    private String approvedByUserId;

    /**
     * Valyuta (masalan: SO'M, USD, EUR).
     * Xarid summasi qaysi pul birligida yuritilishini ko‘rsatadi.
     */
    private String currency;

    /**
     * Qarzni to‘lash muddati (due date).
     * Agar to‘lov kechiktirilsa, qarzdorlik hisoblanadi.
     */
    private LocalDate dueDate;

    /**
     * Buyurtmaning statusi:
     * - DRAFT: yangi yaratilgan, hali tasdiqlanmagan.
     * - APPROVED: rahbar tomonidan tasdiqlangan.
     * - CANCELLED: bekor qilingan.
     */
    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;

    /**
     * To‘lov statusi:
     * - UNPAID: to‘lov qilinmagan.
     * - PARTIAL: qisman to‘langan.
     * - PAID: to‘liq to‘langan.
     */
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    /**
     * Xaridning umumiy summasi (buyurtma qiymati).
     * Buyurtmaga kiritilgan tovarlar asosida hisoblanadi.
     */
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * To‘langan summa.
     * Xaridor to‘lagan miqdor shu yerga yoziladi.
     */
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * Qarzdorlik summasi (totalAmount - paidAmount).
     * Agar UNPAID bo‘lsa → totalAmount.
     * Agar PARTIAL bo‘lsa → qoldiq summa.
     * Agar PAID bo‘lsa → 0.
     */
    private BigDecimal debtAmount = BigDecimal.ZERO;

    /**
     * Qo‘shimcha izohlar, izohli matn.
     * Masalan: "Aksiyadagi mahsulotlar uchun buyurtma".
     */
    @Column(columnDefinition = "TEXT")
    private String comment;


    public enum Status {DRAFT, APPROVED, CANCELLED}

    public enum PaymentStatus {UNPAID, PARTIAL, PAID}
}
