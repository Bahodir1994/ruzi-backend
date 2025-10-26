package app.ruzi.entity.tasks;

import app.ruzi.configuration.utils.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "document", schema = "ruzi")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Document extends AbstractAuditingEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(length = 50)
    private String parentId;

    @Column(length = 200)
    private String docName;

    @Column(length = 100)
    private String docType;

    @Column(length = 300)
    private String hash;

    @Column(length = 50)
    private String fileNum;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date fileDate;

    @Column(length = 200)
    private String docNameUni;
}
