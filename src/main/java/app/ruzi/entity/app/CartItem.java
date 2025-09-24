//package app.ruzi.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.math.BigDecimal;
//
//@Entity
//@Table(name = "cart_items", schema = "ruzi", uniqueConstraints = {
//        @UniqueConstraint(columnNames = {"cart_id", "product_id"})
//})
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class CartItem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "cart_id", nullable = false)
//    private CartSession cartSession;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
//
//    private Integer quantity;
//
//    private BigDecimal unitPrice;
//
//    private BigDecimal discount; // har bir mahsulot uchun chegirma
//
//    private BigDecimal lineTotal; // (quantity * unitPrice) - discount
//}
//
