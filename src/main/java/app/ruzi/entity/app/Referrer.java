package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "referrer", schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "referrer_code"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referrer extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 50)
    private String id;

    /** Qaysi klient tizimiga tegishli (multi-tenant) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /** Ustaga berilgan unikal kod (masalan: USTA-001, REF-9955) */
    @Column(name = "referrer_code", length = 50, nullable = false)
    private String referrerCode;

    /** Usta ismi yoki tashkilot nomi */
    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;

    /** Telefon, aloqa ma’lumoti */
    private String phone;

    /** Joriy bonus balansi */
    @Column(precision = 18, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
}

