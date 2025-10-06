package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "referral", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referral extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Qaysi klient tizimiga tegishli
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Qaysi sotuv bo‘yicha bonus hisoblanmoqda
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartSession cartSession;

    /**
     * Qaysi usta foydasiga bonus
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id", nullable = false)
    private Referrer referrer;

    /**
     * Bonus summasi (masalan: 2% yoki 5% miqdorda)
     */
    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal bonusAmount;

    /**
     * Bonus foizi
     */
    @Column(precision = 5, scale = 2)
    private BigDecimal bonusPercent;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status = Status.PENDING;

    public enum Status {PENDING, APPROVED, PAID}
}
