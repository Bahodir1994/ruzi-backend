package app.ruzi.entity.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "unit", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {
    @Id
    @Column(length = 3)
    private String code;

    @Column(nullable = false)
    private String nameEn;

    @Column(nullable = false)
    private String nameRu;

    @Column(nullable = false)
    private String nameOz;

    @Column(nullable = false)
    private String nameUz;

    @Column(nullable = false)
    private String name;

}
