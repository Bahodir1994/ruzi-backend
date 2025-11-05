package app.ruzi.configuration.annotation.uniqueCategory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, Object> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<?> entityClass;
    private String[] entityFields;

    @Override
    public void initialize(UniqueField constraintAnnotation) {
        this.entityClass = constraintAnnotation.entityClass();
        this.entityFields = constraintAnnotation.fieldEntity();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // Agar DTOdagi qiymat null bo‘lsa — tekshirmaymiz
        if (value == null) {
            return true;
        }

        try {
            // e.field1 = :val OR e.field2 = :val ... kabi shartlar yaratamiz
            String whereClause = Arrays.stream(entityFields)
                    .map(f -> "LOWER(e." + f + ") = LOWER(:val)")
                    .collect(Collectors.joining(" OR "));

            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE " + whereClause;

            Query query = entityManager.createQuery(jpql);
            query.setParameter("val", value);

            Long count = (Long) query.getSingleResult();

            if (count > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addConstraintViolation();
                return false;
            }

            return true;

        } catch (Exception e) {
            // Har qanday xatolikda false qaytarish emas, logda ko‘rsatish foydali
            e.printStackTrace();
            return false;
        }
    }
}
