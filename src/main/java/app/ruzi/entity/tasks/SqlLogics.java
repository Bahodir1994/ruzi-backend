package app.ruzi.entity.tasks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;

@Entity
@Table(name = "sql_logics", schema = "ruzi")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SqlLogics {
    @Id
    @GeneratedValue(generator = "uuid4")
    @Column(name = "id", length = 50)
    private @UUID(version = {4}) String id;

    @Column(name = "logic_name")
    private String logicName;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "query_params")
    private String queryParams;

    @Lob
    @Column(name = "query_text")
    private String queryText;

    @Column(name = "message_code", length = 50)
    private String messageCode;
}
