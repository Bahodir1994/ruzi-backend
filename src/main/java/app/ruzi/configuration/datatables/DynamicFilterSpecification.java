package app.ruzi.configuration.datatables;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DynamicFilterSpecification<T> implements Specification<T> {

    private final String fieldName;
    private final String filterValue;

    public DynamicFilterSpecification(String fieldName, String filterValue) {
        this.fieldName = fieldName;
        this.filterValue = filterValue;
    }

    @Override
    public Predicate toPredicate(@NotNull Root<T> root, CriteriaQuery<?> query, @NotNull CriteriaBuilder cb) {
        if (filterValue == null || filterValue.isBlank()) {
            return cb.conjunction();
        }

        List<Predicate> predicates = new ArrayList<>();
        String operator;

        String[] parts;
        if (filterValue.contains("||")) {
            operator = "or";
            parts = filterValue.split("\\|\\|");
        } else if (filterValue.contains("&&")) {
            operator = "and";
            parts = filterValue.split("&&");
        } else {
            operator = "or"; // по умолчанию
            parts = new String[]{filterValue};
        }

        for (String part : parts) {
            String[] split = part.split(":", 2);
            if (split.length != 2) continue;

            String matchMode = split[0];
            String value = split[1];

            switch (matchMode) {
                case "startsWith":
                    predicates.add(cb.like(cb.lower(root.get(fieldName)), value.toLowerCase() + "%"));
                    break;
                case "contains":
                    predicates.add(cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%"));
                    break;
                case "notContains":
                    predicates.add(cb.notLike(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%"));
                    break;
                case "endsWith":
                    predicates.add(cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase()));
                    break;
                case "equals":
                    if (value.equals("true")) {
                        predicates.add(cb.equal(root.get(fieldName), true));
                    } else if (value.equals("false")) {
                        predicates.add(cb.equal(root.get(fieldName), false));
                    } else {
                        predicates.add(cb.equal(cb.lower(root.get(fieldName)), value.toLowerCase()));
                    }
                    break;
                case "notEquals":
                    predicates.add(cb.notEqual(cb.lower(root.get(fieldName)), value.toLowerCase()));
                    break;
                case "in":
                    List<String> inValues = new ArrayList<>();
                    for (String partIn : parts) {
                        String[] splitIn = partIn.split(":", 2);
                        if (splitIn.length == 2 && "in".equals(splitIn[0])) {
                            inValues.add(splitIn[1]);
                        }
                    }

                    if (!inValues.isEmpty()) {
                        predicates.add(root.get(fieldName).in(inValues));
                    }
                    break;
                default:
                    predicates.add(cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%"));
                    break;
            }
        }

        return "and".equals(operator)
                ? cb.and(predicates.toArray(new Predicate[0]))
                : cb.or(predicates.toArray(new Predicate[0]));
    }
}
