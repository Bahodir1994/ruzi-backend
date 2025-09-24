package app.ruzi.configuration.utils;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
@EntityListeners(CustomAuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class AbstractAuditingEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "ins_user", length = 50, updatable = false)
    private String insUser;

    @LastModifiedBy
    @Column(name = "upd_user", length = 50)
    private String updUser;

    @CreationTimestamp
    @Column(name = "ins_time", updatable = false, nullable = false)
    private Timestamp insTime;

    @UpdateTimestamp
    @Column(name = "upd_time")
    private Timestamp updTime;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
