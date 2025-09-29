package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(
        name = "category",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"client_id", "code"})
        },
        indexes = {
                @Index(name = "idx_category_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryTranslation> translations;

}
