package app.ruzi.service.app.purchase;

import app.ruzi.configuration.datatables.DateRangeSpecification;
import app.ruzi.configuration.datatables.DynamicFilterSpecification;
import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.entity.app.Item;
import app.ruzi.entity.app.PurchaseOrder;
import app.ruzi.entity.app.PurchaseOrderItem;
import app.ruzi.repository.app.ItemRepository;
import app.ruzi.repository.app.PurchaseOrderItemRepository;
import app.ruzi.repository.app.PurchaseOrderRepository;
import app.ruzi.service.enums.DefValues;
import app.ruzi.service.payload.app.CreatePurchaseOrderItemDto;
import app.ruzi.service.payload.app.PurchaseOrderCreatReadDto;
import app.ruzi.service.payload.app.UpdateFieldDto;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService implements PurchaseOrderServiceImplement {
    private final JwtUtils jwtUtils;

    private final OrderNumberGeneratorService orderNumberGeneratorService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public PurchaseOrder create(PurchaseOrderCreatReadDto requestDTO) {

        UserJwt userJwt = jwtUtils.extractUserFromToken();

        PurchaseOrder newOrder = new PurchaseOrder();
        newOrder.setStatus(PurchaseOrder.Status.DRAFT);
        newOrder.setCreatedByUserId(userJwt.getFullName());

        // Auto-generate order number
        newOrder.setOrderNumber(orderNumberGeneratorService.generateOrderNumber(userJwt.getClientId()));

        purchaseOrderRepository.save(newOrder);

        return newOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrder read(String id) {
        return purchaseOrderRepository.findById(id).orElse(null);
    }

    @Override
    public void update(PurchaseOrderCreatReadDto requestDTO) {

    }

    @Override
    @Transactional
    public void updateItem(String id, UpdateFieldDto dto) {

        PurchaseOrderItem item = purchaseOrderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        switch (dto.field()) {
            case "quantity"       -> item.setQuantity(new BigDecimal(dto.value().toString()));
            case "purchasePrice"  -> item.setPurchasePrice(new BigDecimal(dto.value().toString()));
            case "discount"       -> item.setDiscount(new BigDecimal(dto.value().toString()));
            case "salePrice"      -> item.setSalePrice(new BigDecimal(dto.value().toString()));
            case "altSalePrice"   -> item.setAltSalePrice(new BigDecimal(dto.value().toString()));
            case "minimalSum"     -> item.setMinimalSum(new BigDecimal(dto.value().toString()));
            case "unitCode"       -> item.setUnitCode(dto.value().toString());
            case "altUnitCode"    -> item.setAltUnitCode(dto.value().toString());
            case "conversionRate" -> item.setConversionRate(new BigDecimal(dto.value().toString()));
            case "batchNumber"    -> item.setBatchNumber(dto.value().toString());
            case "expiryDate" -> {
                String dt = dto.value().toString();
                LocalDate localDate = OffsetDateTime.parse(dt).toLocalDate();
                item.setExpiryDate(localDate);
            }
            case "packageCount"   -> item.setPackageCount(Integer.parseInt(dto.value().toString()));
        }

        // avtomatik sum hisoblash
        item.setSum(
                item.getQuantity().multiply(item.getPurchasePrice())
                        .subtract(item.getDiscount() == null ? BigDecimal.ZERO : item.getDiscount())
        );

        purchaseOrderItemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteOrder(String orderId) {
        if(purchaseOrderRepository.existsByIdAndStatus(orderId, PurchaseOrder.Status.DRAFT)){
            purchaseOrderRepository.deleteByIdAndStatus(orderId, PurchaseOrder.Status.DRAFT);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBID0001");
        }
    }

    @Override
    @Transactional
    public void deleteItemFromOrder(String orderId, String itemId) {
        if(purchaseOrderRepository.isDraft(orderId, PurchaseOrder.Status.DRAFT)){
            purchaseOrderItemRepository.deleteById(itemId);
        }
    }

    @Override
    @Transactional
    public void addItemToOrder(CreatePurchaseOrderItemDto dto) {

        PurchaseOrder order = purchaseOrderRepository.findById(dto.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        // Faqat default qiymatlar bilan yaratamiz
        PurchaseOrderItem poi = PurchaseOrderItem.builder()
                .purchaseOrder(order)
                .item(item)
                .client(order.getClient())
                .unitCode(item.getUnit() != null ? item.getUnit() : DefValues.UNIT_DEF.getValue())
                .altUnitCode(item.getUnit() != null ? item.getUnit() : DefValues.UNIT_DEF.getValue())
                //.conversionRate(null)
                .packageCount(0)
                .quantity(BigDecimal.ZERO)
                .purchasePrice(BigDecimal.ZERO)
                .salePrice(null)
                .altSalePrice(null)
                .discount(BigDecimal.ZERO)
                .sum(BigDecimal.ZERO)
                .build();

        purchaseOrderItemRepository.save(poi);
    }


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
    @Transactional(readOnly = true)
    public DataTablesOutput<PurchaseOrderItem> readTablePurchaseOrderItem(DataTablesInput dataTablesInput) {
        return purchaseOrderItemRepository.findAll(dataTablesInput);
    }
}
