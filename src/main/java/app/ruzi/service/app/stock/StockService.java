package app.ruzi.service.app.stock;

import app.ruzi.entity.app.*;
import app.ruzi.repository.app.PurchaseOrderItemRepository;
import app.ruzi.repository.app.StockRepository;
import app.ruzi.repository.app.UnitRepository;
import app.ruzi.service.mappers.StockMapper;
import app.ruzi.service.payload.app.StockViewDto;
import jakarta.persistence.LockModeType;
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
    private final StockWebSocketService wsService;

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

            Integer qty = poi.getQuantity() != null ? poi.getPackageCount() : 0;
            BigDecimal altQty = BigDecimal.valueOf(qty).multiply(poi.getConversionRate());


            Stock stock = Stock.builder()
                    .client(order.getClient())
                    .purchaseOrderItem(poi)
                    .warehouse(order.getWarehouse())
                    .quantity(BigDecimal.valueOf(qty))
                    .altQuantity(altQty)
                    .reservedQuantity(BigDecimal.ZERO)
                    .reservedAltQuantity(BigDecimal.ZERO)
                    .build();

            stockRepository.save(stock);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void writeOffItem(CartItem item) {

        String poiId = item.getPurchaseOrderItem().getId();
        String whId = item.getWarehouse().getId();

        BigDecimal qty = item.getQuantity();
        BigDecimal altQty = qty.multiply(item.getPurchaseOrderItem().getConversionRate());

        Stock stock = stockRepository.findByPurchaseOrderItemAndWarehouse_Locked(
                poiId, whId, LockModeType.PESSIMISTIC_WRITE
        ).orElseThrow(() -> new IllegalStateException("Stock not found"));

        // real chiqim
        stock.setQuantity(stock.getQuantity().subtract(qty));
        stock.setAltQuantity(stock.getAltQuantity().subtract(altQty));

        // reserved nol bo‘ladi
        stock.setReservedQuantity(stock.getReservedQuantity().subtract(qty));
        stock.setReservedAltQuantity(stock.getReservedAltQuantity().subtract(altQty));

        if (stock.getReservedQuantity().compareTo(BigDecimal.ZERO) < 0)
            stock.setReservedQuantity(BigDecimal.ZERO);
        if (stock.getReservedAltQuantity().compareTo(BigDecimal.ZERO) < 0)
            stock.setReservedAltQuantity(BigDecimal.ZERO);

        stockRepository.save(stock);
        wsService.broadcastStockUpdate(toDto(stock));
    }

    /**
     * yordamchi - natijani dto ga set qilish
     */
    private StockViewDto toDto(Stock stock) {
        BigDecimal available = stock.getQuantity().subtract(stock.getReservedQuantity());
        BigDecimal availableAlt = stock.getAltQuantity().subtract(stock.getReservedAltQuantity());

        return StockViewDto.builder()
                .stockId(stock.getId())
                .quantity(stock.getQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .availableQuantity(available)

                .altQuantity(stock.getAltQuantity())
                .reservedAltQuantity(stock.getReservedAltQuantity())
                .availableAltQuantity(availableAlt)
                .conversionRate(stock.getPurchaseOrderItem().getConversionRate()) // 🆕 qo‘shildi

                .itemName(stock.getPurchaseOrderItem().getItem().getName())
                .warehouseName(stock.getWarehouse().getName())
                .build();
    }
}
