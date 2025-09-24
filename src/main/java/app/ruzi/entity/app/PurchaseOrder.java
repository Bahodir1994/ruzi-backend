//package app.ruzi.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
///**
// * PurchaseOrder (Xarid buyurtmasi) entitiy.
// *
// * Bu jadval orqali tashkilotda xarid buyurtmalari boshqariladi.
// * Har bir xarid buyurtmasi ta'minotchi, ombor, yaratuvchi va
// * tasdiqlovchi xodim bilan bog‘lanadi hamda umumiy summasi,
// * to‘langan qismi va qarzi hisoblanadi.
// */
//@Entity
//@Table(name = "purchase_orders", schema = "ruzi")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class PurchaseOrder {
//
//    /**
//     * Primary Key (avtomatik generatsiya qilinadi).
//     */
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    /**
//     * Buyurtma raqami (№ hujjat).
//     * Unikal bo‘lishi kerak. Masalan: PO-2025-001.
//     * Buyurtma yaratilganda avtomatik yoki qo‘lda beriladi.
//     */
//    @Column(unique = true, nullable = false, length = 50)
//    private String orderNumber;
//
//    /**
//     * Ta’minotchi (Supplier) bilan bog‘liq.
//     * Xarid kimdan amalga oshirilganini ko‘rsatadi.
//     * Har doim to‘ldirilishi shart.
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "supplier_id", nullable = false)
//    private Supplier supplier;
//
//    /**
//     * Ombor (Warehouse) bilan bog‘liq.
//     * Xarid qaysi omborga kirim qilinishini ko‘rsatadi.
//     * Har doim to‘ldirilishi shart.
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "warehouse_id", nullable = false)
//    private Warehouse warehouse;
//
//    /**
//     * Buyurtma yaratilgan sana.
//     * Hujjat yaratilganda avtomatik yoziladi (system date).
//     */
//    private LocalDate createdAt;
//
//    /**
//     * Buyurtma tasdiqlangan sana.
//     * Faqat manager/admin tomonidan APPROVED bo‘lganda yoziladi.
//     */
//    private LocalDate approvedAt;
//
//    /**
//     * Buyurtmani yaratgan xodim.
//     * Xarid buyurtmasini kim kiritganini ko‘rsatadi.
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private Employee createdBy;
//
//    /**
//     * Buyurtmani tasdiqlagan xodim.
//     * Faqat APPROVED statusiga o‘tganda yoziladi.
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "approved_by")
//    private Employee approvedBy;
//
//    /**
//     * Valyuta (masalan: SO'M, USD, EUR).
//     * Xarid summasi qaysi pul birligida yuritilishini ko‘rsatadi.
//     */
//    private String currency;
//
//    /**
//     * Qarzni to‘lash muddati (due date).
//     * Agar to‘lov kechiktirilsa, qarzdorlik hisoblanadi.
//     */
//    private LocalDate dueDate;
//
//    /**
//     * Buyurtmaning statusi:
//     * - DRAFT: yangi yaratilgan, hali tasdiqlanmagan.
//     * - APPROVED: rahbar tomonidan tasdiqlangan.
//     * - CANCELLED: bekor qilingan.
//     */
//    @Enumerated(EnumType.STRING)
//    private Status status = Status.DRAFT;
//
//    /**
//     * To‘lov statusi:
//     * - UNPAID: to‘lov qilinmagan.
//     * - PARTIAL: qisman to‘langan.
//     * - PAID: to‘liq to‘langan.
//     */
//    @Enumerated(EnumType.STRING)
//    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;
//
//    /**
//     * Xaridning umumiy summasi (buyurtma qiymati).
//     * Buyurtmaga kiritilgan tovarlar asosida hisoblanadi.
//     */
//    private BigDecimal totalAmount = BigDecimal.ZERO;
//
//    /**
//     * To‘langan summa.
//     * Xaridor to‘lagan miqdor shu yerga yoziladi.
//     */
//    private BigDecimal paidAmount = BigDecimal.ZERO;
//
//    /**
//     * Qarzdorlik summasi (totalAmount - paidAmount).
//     * Agar UNPAID bo‘lsa → totalAmount.
//     * Agar PARTIAL bo‘lsa → qoldiq summa.
//     * Agar PAID bo‘lsa → 0.
//     */
//    private BigDecimal debtAmount = BigDecimal.ZERO;
//
//    /**
//     * Qo‘shimcha izohlar, izohli matn.
//     * Masalan: "Aksiyadagi mahsulotlar uchun buyurtma".
//     */
//    @Column(columnDefinition = "TEXT")
//    private String comment;
//
//    public enum Status { DRAFT, APPROVED, CANCELLED }
//    public enum PaymentStatus { UNPAID, PARTIAL, PAID }
//}
