package app.ruzi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouses", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String address;

    private String type; // MAIN, BRANCH, MOBILE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;
}
