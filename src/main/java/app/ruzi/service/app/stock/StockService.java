package app.ruzi.service.app.stock;

import app.ruzi.entity.app.*;
import app.ruzi.repository.app.PurchaseOrderItemRepository;
import app.ruzi.repository.app.StockRepository;
import app.ruzi.repository.app.UnitRepository;
import app.ruzi.service.mappers.StockMapper;
import app.ruzi.service.payload.app.StockViewDto;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService implements StockServiceImplement {
    private final StockRepository stockRepository;
    private final UnitRepository unitRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Override
    public DataTablesOutput<StockViewDto> getStock(DataTablesInput dataTablesInput) {
        String searchValue = dataTablesInput.getSearch().getValue();

        Specification<Stock> specification = (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                Fetch<Stock, PurchaseOrderItem> poiFetch = root.fetch("purchaseOrderItem", JoinType.LEFT);
                Fetch<PurchaseOrderItem, Item> itemFetch = poiFetch.fetch("item", JoinType.LEFT);
                itemFetch.fetch("category", JoinType.LEFT);
                root.fetch("warehouse", JoinType.LEFT);
                query.distinct(true);
            }

            Predicate predicate = cb.conjunction();

            if (searchValue != null && !searchValue.trim().isEmpty()) {
                Join<Object, Object> itemJoin = root.join("purchaseOrderItem", JoinType.LEFT).join("item", JoinType.LEFT);
                Predicate namePredicate = cb.like(cb.lower(itemJoin.get("name")), "%" + searchValue.toLowerCase() + "%");
                Predicate barcodePredicate = cb.like(cb.lower(itemJoin.get("barcode")), "%" + searchValue.toLowerCase() + "%");
                predicate = cb.or(namePredicate, barcodePredicate);
            }

            return predicate;
        };

        DataTablesOutput<Stock> repositoryAll = stockRepository.findAll(dataTablesInput, specification);
        List<StockViewDto> dtoList = StockMapper.INSTANCE.toDtoList(repositoryAll.getData());

        // 🧠 2-qadam: bir marta DB dan barcha unitlarni olib kelamiz
        List<Unit> units = unitRepository.findAll();

        // 🧠 3-qadam: map yaratamiz
        Map<String, String> codeToName = units.stream()
                .collect(Collectors.toMap(Unit::getCode, Unit::getName));

        // 🧠 4-qadam: dto’larni update qilamiz
        dtoList.forEach(dto -> {
            String unitCode = dto.getUnitName();
            String altUnitCode = dto.getAltUnitName();

            if (unitCode != null && codeToName.containsKey(unitCode)) {
                dto.setUnitName(codeToName.get(unitCode));
            }
            if (altUnitCode != null && codeToName.containsKey(altUnitCode)) {
                dto.setAltUnitName(codeToName.get(altUnitCode));
            }
        });

        // 🔹 Yangi DTO output
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

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createStockForApprovedPurchaseOrder(PurchaseOrder order) {

        List<PurchaseOrderItem> items =
                purchaseOrderItemRepository.findByPurchaseOrder_Id(order.getId());

        for (PurchaseOrderItem poi : items) {

            // Agar stock yozuvi mavjud bo‘lsa — yaratmaymiz
            boolean exists = stockRepository.existsByPurchaseOrderItem_IdAndWarehouse_Id(
                    poi.getId(),
                    order.getWarehouse().getId()
            );

            if (exists) continue;

            BigDecimal qty = poi.getQuantity() != null ? poi.getQuantity() : BigDecimal.ZERO;
            BigDecimal altQty = poi.getConversionRate() != null
                    ? qty.multiply(poi.getConversionRate())
                    : qty;

            Stock stock = Stock.builder()
                    .client(order.getClient())
                    .purchaseOrderItem(poi)
                    .warehouse(order.getWarehouse())
                    .quantity(qty)
                    .altQuantity(altQty)
                    .reservedQuantity(BigDecimal.ZERO)
                    .reservedAltQuantity(BigDecimal.ZERO)
                    .build();

            stockRepository.save(stock);
        }
    }
}
