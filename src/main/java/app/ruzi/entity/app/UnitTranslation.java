package app.ruzi.entity.app;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "unit_translation",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"unit_code", "language_code"})
        },
        indexes = {
                @Index(name = "idx_unit_translation_unit_code", columnList = "unit_code"),
                @Index(name = "idx_unit_translation_language_code", columnList = "language_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_code", referencedColumnName = "code", nullable = false)
    private Unit unit;

    @Column(name = "language_code", length = 3, nullable = false)
    private String languageCode; // en, ru, uz, oz

    @Column(name = "name", length = 100, nullable = false)
    private String name;
}

