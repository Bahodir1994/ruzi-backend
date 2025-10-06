package app.ruzi.service.app.stock;

import app.ruzi.entity.app.Stock;
import app.ruzi.repository.app.StockRepository;
import app.ruzi.service.mappers.StockMapper;
import app.ruzi.service.payload.app.StockViewDto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService implements StockServiceImplement {
    private final StockRepository stockRepository;

    @Override
    public DataTablesOutput<StockViewDto> getStock(DataTablesInput dataTablesInput) {
        String searchValue = dataTablesInput.getSearch().getValue();

        Specification<Stock> specification = (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("purchaseOrderItem", JoinType.LEFT).fetch("item", JoinType.LEFT);
                root.fetch("warehouse", JoinType.LEFT);
                query.distinct(true);
            }

            Predicate predicate = cb.conjunction();

            if (searchValue != null && !searchValue.trim().isEmpty()) {
                // Item join
                Join<Object, Object> itemJoin = root.join("purchaseOrderItem", JoinType.LEFT).join("item", JoinType.LEFT);

                // item.name LIKE %searchValue%
                Predicate namePredicate = cb.like(
                        cb.lower(itemJoin.get("name")),
                        "%" + searchValue.toLowerCase() + "%"
                );

                // item.barcode LIKE %searchValue%
                Predicate barcodePredicate = cb.like(
                        cb.lower(itemJoin.get("barcode")),
                        "%" + searchValue.toLowerCase() + "%"
                );

                // OR birlashtirish
                predicate = cb.or(namePredicate, barcodePredicate);
            }

            return predicate;
        };

        DataTablesOutput<Stock> repositoryAll = stockRepository.findAll(dataTablesInput, specification);
        List<StockViewDto> dtoList = StockMapper.INSTANCE.toDtoList(repositoryAll.getData());

        DataTablesOutput<StockViewDto> output = new DataTablesOutput<>();
        output.setDraw(repositoryAll.getDraw());
        output.setRecordsFiltered(repositoryAll.getRecordsFiltered());
        output.setRecordsTotal(repositoryAll.getRecordsTotal());
        output.setError(repositoryAll.getError());
        output.setData(dtoList);

        return output;
    }


    @Override
    public List<Stock> getStockList() {
        return stockRepository.findAll();
    }

}
