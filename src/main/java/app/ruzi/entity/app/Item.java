package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "item",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"category_id", "code"})
        },
        indexes = {
                @Index(name = "idx_item_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "price")
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "primary_image_url", length = 300)
    private String primaryImageUrl;
}


