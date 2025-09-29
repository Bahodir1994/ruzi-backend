package app.ruzi.entity.app;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(
        name = "unit",
        schema = "ruzi"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {

    @Id
    @Column(name = "code", length = 10, nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UnitTranslation> translations;
}
