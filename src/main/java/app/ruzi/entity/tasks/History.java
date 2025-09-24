package app.ruzi.entity.tasks;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "history", schema = "ruzi")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class History extends AbstractAuditingEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "status", length = 3)
    private String status;

    @Column(name = "ins_user_name", length = 120)
    private String insUserName;

    @Column(name = "comment", length = 1800)
    private String comment;
}

