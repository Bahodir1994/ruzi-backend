package app.ruzi.configuration.datatables;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class OrderBuilder<T> {

    private final CriteriaBuilder cb;
    private final Root<T> root;
    private final Map<String, Function<Root<T>, Expression<?>>> columnExpressionMap;

    public OrderBuilder(CriteriaBuilder cb,
                        Root<T> root,
                        Map<String, Function<Root<T>, Expression<?>>> columnExpressionMap) {
        this.cb = cb;
        this.root = root;
        this.columnExpressionMap = columnExpressionMap;
    }

    public List<Order> buildOrders(DataTablesInput input) {
        List<Order> orders = new ArrayList<>();

        for (org.springframework.data.jpa.datatables.mapping.Order orderColumn : input.getOrder()) {
            Column column = input.getColumns().get(orderColumn.getColumn());
            if (column.getOrderable()) {
                String columnName = column.getData();
                Function<Root<T>, Expression<?>> expressionBuilder = columnExpressionMap.get(columnName);
                if (expressionBuilder != null) {
                    Expression<?> expression = expressionBuilder.apply(root);
                    orders.add("desc".equalsIgnoreCase(orderColumn.getDir()) ? cb.desc(expression) : cb.asc(expression));
                }
            }
        }

        return orders;
    }
}


