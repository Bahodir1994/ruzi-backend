package app.ruzi.entity.tasks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "document_hash", schema = "ruzi")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DocumentHash {

    @Id
    @Column(length = 300, nullable = false)
    private String hash;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    private Date insTime = new Date();
}
