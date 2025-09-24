package app.ruzi.configuration.utils;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class CustomAuditingEntityListener {
    @PrePersist
    public void setCreatedBy(Object entity) {
        if (entity instanceof AbstractAuditingEntity auditingEntity) {
            auditingEntity.setInsUser(CurrentUserProvider.getCurrentUser());
        }
    }

    @PreUpdate
    public void setUpdatedBy(Object entity) {
        if (entity instanceof AbstractAuditingEntity auditingEntity) {
            auditingEntity.setUpdUser(CurrentUserProvider.getCurrentUser());
        }
    }
}
