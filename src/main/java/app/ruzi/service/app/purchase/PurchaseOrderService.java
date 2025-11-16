package app.ruzi.service.app.purchase;

import app.ruzi.configuration.datatables.DateRangeSpecification;
import app.ruzi.configuration.datatables.DynamicFilterSpecification;
import app.ruzi.entity.app.PurchaseOrder;
import app.ruzi.repository.app.PurchaseOrderRepository;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService implements PurchaseOrderServiceImplement {

    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<PurchaseOrder> readTablePurchaseOrder(DataTablesInput dataTablesInput) {
        AtomicReference<String> fullName = new AtomicReference<>("");

        List<Specification<PurchaseOrder>> specs = new ArrayList<>();
        dataTablesInput.getColumns().forEach(column -> {
            String searchValue = column.getSearch().getValue();
            if (column.getSearchable() && searchValue != null && !searchValue.isBlank()) {
                specs.add(new DynamicFilterSpecification<>(column.getData(), searchValue));
            }
            column.getSearch().setValue("");
        });
        Specification<PurchaseOrder> combinedSpec = (root, query, criteriaBuilder) -> {
            root.fetch("supplier", JoinType.LEFT);
            root.fetch("warehouse", JoinType.LEFT);
            root.fetch("createdBy", JoinType.LEFT);
            root.fetch("approvedBy", JoinType.LEFT);
            return null;
        };

        Specification<PurchaseOrder> dateSpec = new DateRangeSpecification<>(
                dataTablesInput, "createdAt", "createdAt"
        );
        for (Specification<PurchaseOrder> spec : specs) {
            dateSpec = dateSpec.and(spec);
        }

        combinedSpec.and(dateSpec);
        return purchaseOrderRepository.findAll(dataTablesInput, dateSpec);
    }

    @Override
    public void dasdasdasd() {

    }
}
