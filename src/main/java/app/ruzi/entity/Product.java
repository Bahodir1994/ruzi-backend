package app.ruzi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String skuCode; // artikl yoki shtrix-kod

    @Column(unique = true, length = 100)
    private String barcode; // EAN/UPC

    @Column(nullable = false, length = 255)
    private String name;

    private String brand;

    private String category; // masalan: elektronika, kiyim

    private String unit; // dona, kg, litr

    private BigDecimal defaultSalePrice; // asosiy sotuv narxi

    private String description;

    private Boolean isActive = true; // sotuvdan chiqarilgan yoki yo‘q
}

