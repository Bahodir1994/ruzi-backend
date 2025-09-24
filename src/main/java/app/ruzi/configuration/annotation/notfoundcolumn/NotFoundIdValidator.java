package app.ruzi.configuration.annotation.notfoundcolumn;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class NotFoundIdValidator implements ConstraintValidator<NotFoundId, String> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<?> entityClass;
    private String columnName;
    private boolean nullable;  // Add a boolean to store the nullable attribute
    private String[] defaultValue;  // Add a boolean to store the nullable attribute

    @Override
    public void initialize(NotFoundId constraintAnnotation) {
        this.entityClass = constraintAnnotation.entityName();
        this.columnName = constraintAnnotation.searchColumnName();
        this.nullable = constraintAnnotation.nullable();  // Set the nullable attribute
        this.defaultValue = constraintAnnotation.defaultValue();  // Set the nullable attribute
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return nullable;  // Return based on the nullable attribute
        }

        if (Arrays.asList(defaultValue).contains(value)) {
            return true;
        }

        String queryStr = "SELECT COUNT(*) FROM " + entityClass.getSimpleName() +
                " WHERE " + columnName + " = :id";

        Query query = entityManager.createQuery(queryStr);
        query.setParameter("id", value);

        Long count = (Long) query.getSingleResult();

        return count > 0;  // If count > 0, the entity with the given ID exists
    }
}


