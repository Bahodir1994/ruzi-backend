//package app.ruzi.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@Entity
//@Table(name = "purchase_order_items", schema = "ruzi")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class PurchaseOrderItem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "purchase_order_id", nullable = false)
//    private PurchaseOrder purchaseOrder;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    private Integer quantity;
//
//    private String unit; // kg, dona, litr (agar product.unit ustunini ishlatmasangiz)
//
//    private BigDecimal purchasePrice;
//    private BigDecimal salePrice;
//    private BigDecimal sum;
//
//    private BigDecimal discount; // chegirma summasi
//
//    private String batchNumber; // seriya raqami
//
//    private LocalDate expiryDate; // yaroqlilik muddati
//}
//
