package app.ruzi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "return_orders", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi savatcha/chekka bog‘langan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_session_id", nullable = false)
    private CartSession cartSession;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Qaytarishni amalga oshirgan xodim
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private BigDecimal totalReturnAmount = BigDecimal.ZERO;

    private String reason; // qaytarish sababi (nuqson, noto‘g‘ri buyurtma va h.k.)

    @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnItem> items;
}

