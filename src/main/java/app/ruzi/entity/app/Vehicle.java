package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Mijozga tegishli transport vositasi — asosan servis yoki avtomobil xizmatlari uchun.
 */
@Entity
@Table(name = "vehicle", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends AbstractAuditingEntity {

    /** Birlamchi identifikator (avto inkrement) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Aloqador mijoz (Customer) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /** Davlat raqami (masalan: 01A123BB) */
    @Column(name = "plate_number", length = 20, nullable = false)
    private String plateNumber;

    /** VIN kodi (kuzov raqami) */
    @Column(name = "vin_code", length = 50)
    private String vinCode;

    /** Avtomobil brendi (Toyota, Chevrolet, Hyundai ...) */
    @Column(name = "brand", length = 120)
    private String brand;

    /** Model nomi (Camry, Cobalt, Elantra ...) */
    @Column(name = "model", length = 120)
    private String model;

    /** Rang nomi */
    @Column(name = "color", length = 60)
    private String color;

    /** Ishlab chiqarilgan yil */
    @Column(name = "year_made")
    private Integer yearMade;

    /** Dvigatel raqami */
    @Column(name = "engine_number", length = 100)
    private String engineNumber;

    /** Bosib o‘tilgan masofa (km) */
    @Column(name = "mileage")
    private Double mileage;

    /** So‘nggi texnik xizmat sanasi */
    @Column(name = "last_service_at")
    private java.time.LocalDateTime lastServiceAt;

    /** Izoh yoki qo‘shimcha ma’lumot */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
