//package app.ruzi.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Entity
//@Table(name = "cart_sessions", schema = "ruzi")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class CartSession {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", nullable = false)
//    private Employee employee;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "warehouse_id", nullable = false)
//    private Warehouse warehouse;
//
//    @Enumerated(EnumType.STRING)
//    private Status status = Status.OPEN; // OPEN / CHECKED_OUT / CANCELLED
//
//    private LocalDateTime createdAt = LocalDateTime.now();
//    private LocalDateTime closedAt; // yakunlangan vaqt
//
//    private String customerName; // optional: mijoz ismi
//    private Long customerId;     // optional: mijozga ulash
//
//    private BigDecimal totalAmount = BigDecimal.ZERO;
//
//    @OneToMany(mappedBy = "cartSession", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CartItem> items;
//
//    public enum Status { OPEN, CHECKED_OUT, CANCELLED }
//}
