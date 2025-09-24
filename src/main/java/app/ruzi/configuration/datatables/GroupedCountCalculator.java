package app.ruzi.configuration.datatables;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroupedCountCalculator {

    @PersistenceContext
    private EntityManager entityManager;

    public <T> Long countDistinctGrouped(DataTablesInput input,
                                         List<String> groupByFields,
                                         Class<T> entityClass,
                                         Specification<T>... extraSpecifications) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> root = countQuery.from(entityClass);

        // 👇 Объединяем groupBy поля в один Expression<String>
        Expression<String> groupExpr = buildGroupConcatExpression(cb, root, groupByFields);
        countQuery.select(cb.countDistinct(groupExpr));

        List<Predicate> predicates = new ArrayList<>();

        // ✅ Применяем дополнительные фильтры
        for (Specification<T> spec : extraSpecifications) {
            Predicate predicate = spec.toPredicate(root, countQuery, cb);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        // ✅ Поиск по колонкам (из DataTablesInput)
        input.getColumns().forEach(column -> {
            String searchValue = column.getSearch().getValue();
            if (column.getSearchable() && searchValue != null && !searchValue.isBlank()) {
                DynamicFilterSpecification<T> dynamicSpec = new DynamicFilterSpecification<>(column.getData(), searchValue);
                Predicate dynamicPredicate = dynamicSpec.toPredicate(root, countQuery, cb);
                if (dynamicPredicate != null) {
                    predicates.add(dynamicPredicate);
                }
            }
        });

        if (!predicates.isEmpty()) {
            countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private <T> Expression<String> buildGroupConcatExpression(CriteriaBuilder cb, Root<T> root, List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("Group by fields must not be empty");
        }

        Expression<String> result = cb.coalesce(root.get(fields.get(0)).as(String.class), "NULL");
        for (int i = 1; i < fields.size(); i++) {
            Expression<String> next = cb.coalesce(root.get(fields.get(i)).as(String.class), "NULL");
            result = cb.concat(cb.concat(result, ":"), next);
        }
        return result;
    }
}
