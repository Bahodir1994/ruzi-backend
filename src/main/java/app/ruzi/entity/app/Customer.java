package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Universal mijozlar jadvali — do‘kon, servis va xizmat ko‘rsatish sohalarida umumiy ishlatiladi.
 */
@Entity
@Table(name = "customer", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer extends AbstractAuditingEntity {

    /**
     * Birlamchi identifikator (avto inkrement)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    /**
     * Tizimdagi unikal mijoz kodi (masalan: CUST-0001)
     */
    @Column(name = "client_code", length = 50, unique = true, nullable = false)
    private String clientCode;

    /**
     * Mijozning to‘liq ismi yoki tashkilot nomi
     */
    @Column(name = "full_name", length = 255, nullable = false)
    private String fullName;

    /**
     * Telefon raqami
     */
    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    /**
     * Elektron pochta manzili
     */
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Jinsi: MALE, FEMALE, UNKNOWN
     */
    @Column(name = "gender", length = 10)
    private String gender;

    /**
     * Tug‘ilgan sana (agar mavjud bo‘lsa)
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Mijoz turi: RETAIL (do‘kon), SERVICE (servis), CORPORATE (tashkilot)
     */
    @Column(name = "customer_type", length = 20)
    private String customerType;

    /**
     * Kompaniya nomi (korporativ mijozlar uchun)
     */
    @Column(name = "company_name", length = 255)
    private String companyName;

    /**
     * STIR / INN (tashkilotlar uchun)
     */
    @Column(name = "tin", length = 15)
    private String tin;

    /**
     * Mintaqa yoki viloyat nomi
     */
    @Column(name = "region", length = 120)
    private String region;

    /**
     * Manzil (ko‘cha, uy, ofis va h.k.)
     */
    @Column(name = "address", length = 300)
    private String address;

    /**
     * Qo‘shimcha izoh yoki eslatma
     */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    /**
     * Bonus yoki sodiqlik ballari
     */
    @Column(name = "loyalty_points")
    private Double loyaltyPoints;

    /**
     * Mijoz faolligi holati
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Aloqa: mijozga tegishli avtomobillar ro‘yxati
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles;

    public Customer(String id) {
        this.id = id;
    }
}
