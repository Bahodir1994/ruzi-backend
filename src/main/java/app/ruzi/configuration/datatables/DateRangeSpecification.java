package app.ruzi.configuration.datatables;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.springframework.util.StringUtils.hasText;

public class DateRangeSpecification<T> implements Specification<T> {

    private final String fieldName;
    private final LocalDate minDate;
    private final LocalDate maxDate;

    public DateRangeSpecification(DataTablesInput input, String columnName, String fieldName) {
        this.fieldName = fieldName;

        Search columnSearch = input.getColumn(columnName).getSearch();
        String dateFilter = columnSearch.getValue();
        columnSearch.setValue(""); // Prevent default search
        if (!hasText(dateFilter)) {
            minDate = maxDate = null;
            return;
        }

        String[] bounds = dateFilter.split(";");
        minDate = parseDate(bounds, 0);
        maxDate = parseDate(bounds, 1);
    }

    private LocalDate parseDate(String[] bounds, int index) {
        if (bounds.length > index && hasText(bounds[index])) {
            try {
                return LocalDate.parse(bounds[index]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Expression<LocalDate> datePath = root.get(fieldName).as(LocalDate.class);
        if (minDate != null && maxDate != null) {
            return cb.between(datePath, minDate, maxDate);
        } else if (minDate != null) {
            return cb.greaterThanOrEqualTo(datePath, minDate);
        } else if (maxDate != null) {
            return cb.lessThanOrEqualTo(datePath, maxDate);
        }
        return cb.conjunction(); // always true
    }
}
