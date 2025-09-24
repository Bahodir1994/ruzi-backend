//package app.ruzi.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "stock", schema = "ruzi", uniqueConstraints = {
//        @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
//})
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Stock {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "warehouse_id", nullable = false)
//    private Warehouse warehouse;
//
//    @Column(nullable = false)
//    private Integer quantity = 0;              // real qoldiq
//
//    @Column(nullable = false)
//    private Integer reservedQuantity = 0;      // savatlarga rezerv qilingan
//
//    private BigDecimal averagePurchasePrice;
//    private BigDecimal lastPurchasePrice;
//
//    private Integer minThreshold = 0; // minimal zaxira
//    private Integer maxThreshold;     // maksimal qoldiq
//
//    private LocalDateTime lastMovementDate; // oxirgi harakat (kirim/chiqim)
//}
