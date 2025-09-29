package app.ruzi.entity.app;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "category_translation",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"category_id", "language_code"})
        },
        indexes = {
                @Index(name = "idx_category_translation_category_id", columnList = "category_id"),
                @Index(name = "idx_category_translation_language_code", columnList = "language_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "language_code", length = 3, nullable = false)
    private String languageCode;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 250)
    private String description;
}
