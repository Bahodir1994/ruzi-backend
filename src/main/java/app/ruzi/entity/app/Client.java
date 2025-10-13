package app.ruzi.entity.app;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "client",
        schema = "ruzi",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_clients_inn", columnNames = {"inn"}),
                @UniqueConstraint(name = "uk_clients_keycloak_client_id", columnNames = {"keycloak_client_id"})
        },
        indexes = {
                @Index(name = "idx_clients_name", columnList = "name"),
                @Index(name = "idx_clients_expiry_date", columnList = "expiry_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Client extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, length = 50)
    private String id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "inn", unique = true, length = 15)
    private String inn;

    @Column(name = "keycloak_client_id", nullable = false, unique = true, length = 100)
    private String keycloakClientId;

    @Column(name = "plan", nullable = false, length = 50)
    private String plan;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public Client(String clientId) {
        this.id = clientId;
    }
}

