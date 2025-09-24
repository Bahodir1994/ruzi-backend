package app.ruzi.configuration.datatables;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PredicateBuilder<T> {

    private final DataTablesInput input;
    private final CriteriaBuilder cb;
    private final Root<T> root;
    private final CriteriaQuery<?> query;
    private final List<Predicate> predicates = new ArrayList<>();

    public PredicateBuilder(
            DataTablesInput input,
            CriteriaBuilder cb,
            Root<T> root,
            CriteriaQuery<?> query
    ) {
        this.input = input;
        this.cb = cb;
        this.root = root;
        this.query = query;
    }

    public PredicateBuilder<T> withDateSpecification(Specification<T> dateSpec) {
        if (dateSpec != null) {
            Predicate datePredicate = dateSpec.toPredicate(root, query, cb);
            if (datePredicate != null) {
                predicates.add(datePredicate);
            }
        }
        return this;
    }

    public PredicateBuilder<T> withDynamicFilters(Function<Column, Specification<T>> specFactory) {
        for (Column column : input.getColumns()) {
            String searchValue = column.getSearch().getValue();
            if (column.getSearchable() && searchValue != null && !searchValue.isBlank()) {
                Specification<T> spec = specFactory.apply(column);
                if (spec != null) {
                    Predicate p = spec.toPredicate(root, query, cb);
                    if (p != null) predicates.add(p);
                }
            }
        }
        return this;
    }

    public PredicateBuilder<T> withGlobalSearch() {
        String globalValue = input.getSearch().getValue();
        if (globalValue != null && !globalValue.isBlank()) {
            String pattern = "%" + globalValue.trim().toLowerCase() + "%";
            List<Predicate> orPredicates = new ArrayList<>();

            for (Column column : input.getColumns()) {
                if (Boolean.TRUE.equals(column.getSearchable())) {
                    try {
                        orPredicates.add(cb.like(cb.lower(root.get(column.getData()).as(String.class)), pattern));
                    } catch (IllegalArgumentException e) {
                        // Field might not exist or not be a String-compatible field — skip
                    }
                }
            }

            if (!orPredicates.isEmpty()) {
                predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
            }
        }
        return this;
    }

    public PredicateBuilder<T> withPredicate(Predicate customPredicate) {
        if (customPredicate != null) {
            predicates.add(customPredicate);
        }
        return this;
    }

    public List<Predicate> build() {
        return predicates;
    }
}

