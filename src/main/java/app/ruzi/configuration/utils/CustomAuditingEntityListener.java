package app.ruzi.configuration.utils;

import app.ruzi.entity.app.Client;
import app.ruzi.entity.app.Warehouse;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.lang.reflect.Field;

public class CustomAuditingEntityListener {
    @PrePersist
    public void setCreatedBy(Object entity) {
        if (entity instanceof AbstractAuditingEntity auditingEntity) {
            auditingEntity.setInsUser(CurrentUserProvider.getCurrentUser());

            // 🔹 1. CLIENT ni avtomatik set qilish
            String clientId = CurrentTenantProvider.getCurrentClient();
            if (clientId != null) {
                setEntityFieldIfNull(entity, "client", new Client(clientId));
            }

            // 🔹 2. WAREHOUSE ni avtomatik set qilish
            String warehouseId = CurrentWarehouseProvider.getCurrentWarehouse();
            if (warehouseId != null) {
                setEntityFieldIfNull(entity, "warehouse", new Warehouse(warehouseId));
            }
        }
    }

    @PreUpdate
    public void setUpdatedBy(Object entity) {
        if (entity instanceof AbstractAuditingEntity auditingEntity) {
            auditingEntity.setUpdUser(CurrentUserProvider.getCurrentUser());
        }
    }

    /**
     * Reflection yordamida field ni to‘ldirish (agar hozircha null bo‘lsa)
     */
    private void setEntityFieldIfNull(Object entity, String fieldName, Object valueToSet) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object currentValue = field.get(entity);
            if (currentValue == null) {
                field.set(entity, valueToSet);
            }
        } catch (NoSuchFieldException ignored) {
            // Entity da bu field yo‘q – e’tiborga olinmaydi
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set field " + fieldName + " automatically", e);
        }
    }
}
