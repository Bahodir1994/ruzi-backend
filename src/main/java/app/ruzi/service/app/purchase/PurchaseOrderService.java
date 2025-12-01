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
import app.ruzi.service.app.stock.StockService;
import app.ruzi.service.enums.DefValues;
import app.ruzi.service.mappers.PurchaseOrderMapper;
import app.ruzi.service.payload.app.CreatePurchaseOrderItemDto;
import app.ruzi.service.payload.app.PurchaseOrderCreatReadDto;
import app.ruzi.service.payload.app.PurchaseOrderUpdateDto;
import app.ruzi.service.payload.app.UpdateFieldDto;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService implements PurchaseOrderServiceImplement {
    private final JwtUtils jwtUtils;

    private final OrderNumberGeneratorService orderNumberGeneratorService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final ItemRepository itemRepository;
    private final StockService stockService;

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
    @Transactional
    public void update(PurchaseOrderUpdateDto dto) {
        UserJwt user = jwtUtils.extractUserFromToken();

        PurchaseOrder order = purchaseOrderRepository.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        PurchaseOrder temp = PurchaseOrderMapper.INSTANCE.toEntity(dto);
        PurchaseOrderMapper.INSTANCE.partialUpdate(temp, order);

        BigDecimal total = order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount();
        BigDecimal paid = order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();

        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            order.setPaymentStatus(PurchaseOrder.PaymentStatus.UNPAID);
        } else if (paid.compareTo(total) < 0) {
            order.setPaymentStatus(PurchaseOrder.PaymentStatus.PARTIAL);
        } else {
            order.setPaymentStatus(PurchaseOrder.PaymentStatus.PAID);
        }

        BigDecimal debt = total.subtract(paid);
        if (debt.compareTo(BigDecimal.ZERO) < 0) debt = BigDecimal.ZERO;
        order.setDebtAmount(debt);

        // 5) APPROVE bo‘lsa → approvedAt va stock yaratish
        if (PurchaseOrder.Status.APPROVED.name().equals(dto.getStatus())) {
            order.setStatus(PurchaseOrder.Status.APPROVED);
            order.setApprovedAt(LocalDate.now());
            order.setApprovedByUserId(user.getUsername());

            purchaseOrderRepository.save(order);

            // Ombor qoldig‘ini yaratish (faqat approved bo‘lganda)
            stockService.createStockForApprovedPurchaseOrder(order);

            return;
        }

        // 6) Agar approved bo‘lmasa: faqat update
        // createdAt va createdBy o‘zgarmaydi!
        // status dto’dan kelgan bo‘lsa, shu o‘rnatiladi
        if (dto.getStatus() != null) {
            order.setStatus(PurchaseOrder.Status.valueOf(dto.getStatus()));
        }

        purchaseOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateItem(String id, UpdateFieldDto dto) {

        PurchaseOrderItem item = purchaseOrderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        switch (dto.field()) {
            case "quantity" -> item.setQuantity(new BigDecimal(dto.value().toString()));
            case "purchasePrice" -> item.setPurchasePrice(new BigDecimal(dto.value().toString()));
            case "discount" -> item.setDiscount(new BigDecimal(dto.value().toString()));
            case "salePrice" -> {
                item.setSalePrice(new BigDecimal(dto.value().toString()));
                BigDecimal convRate = item.getConversionRate();
                if (convRate == null || convRate.compareTo(BigDecimal.ZERO) == 0) {
                    convRate = BigDecimal.ONE; // default 1
                }

                item.setAltSalePrice(
                        item.getSalePrice()
                                .divide(convRate, 0, RoundingMode.CEILING)
                );
            }
            case "altSalePrice" ->
                    item.setAltSalePrice(
                            new BigDecimal(dto.value().toString())
                                    .setScale(0, RoundingMode.CEILING)
                    );
            case "minimalSum" -> item.setMinimalSum(new BigDecimal(dto.value().toString()));
            case "unitCode" -> item.setUnitCode(dto.value().toString());
            case "altUnitCode" -> item.setAltUnitCode(dto.value().toString());
            case "conversionRate" -> {
                BigDecimal convRate = new BigDecimal(dto.value().toString());
                if (convRate.compareTo(BigDecimal.ZERO) == 0) {
                    convRate = BigDecimal.ONE;
                }
                item.setConversionRate(convRate);

                Integer packCount = item.getPackageCount();
                if (packCount == null || packCount == 0) {
                    packCount = 1;
                }

                item.setQuantity(convRate.multiply(BigDecimal.valueOf(packCount)));

                if (item.getSalePrice() != null) {
                    item.setAltSalePrice(
                            item.getSalePrice()
                                    .divide(convRate, 0, RoundingMode.CEILING)
                    );
                }
            }
            case "batchNumber" -> item.setBatchNumber(dto.value().toString());
            case "expiryDate" -> {
                String dt = dto.value().toString();
                LocalDate localDate = OffsetDateTime.parse(dt).toLocalDate();
                item.setExpiryDate(localDate);
            }
            case "packageCount" -> {
                int packCount = Integer.parseInt(dto.value().toString());
                if (packCount == 0) {
                    packCount = 1;
                }
                item.setPackageCount(packCount);

                BigDecimal convRate = item.getConversionRate();
                if (convRate == null || convRate.compareTo(BigDecimal.ZERO) == 0) {
                    convRate = BigDecimal.ONE;
                }
                item.setQuantity(convRate.multiply(BigDecimal.valueOf(packCount)));
            }
        }

        // 1) Item sum ni qayta hisoblaymiz
        BigDecimal price = item.getPurchasePrice() == null ? BigDecimal.ZERO : item.getPurchasePrice();
        BigDecimal qty = item.getQuantity() == null ? BigDecimal.ZERO : item.getQuantity();
        BigDecimal discount = item.getDiscount() == null ? BigDecimal.ZERO : item.getDiscount();

        item.setSum(
                qty.multiply(price).subtract(discount)
        );

        purchaseOrderItemRepository.save(item);

        // 2) Shu item tegishli bo‘lgan order summalarini qayta hisoblaymiz
        PurchaseOrder order = item.getPurchaseOrder();
        // Lazy bo‘lsa ham transaction ichida, muammo yo‘q
        recalculateOrderTotals(order);
    }

    @Override
    @Transactional
    public void deleteOrder(String orderId) {
        if (purchaseOrderRepository.existsByIdAndStatus(orderId, PurchaseOrder.Status.DRAFT)) {
            purchaseOrderRepository.deleteByIdAndStatus(orderId, PurchaseOrder.Status.DRAFT);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBID0001");
        }
    }

    @Override
    @Transactional
    public void deleteItemFromOrder(String orderId, String itemId) {
        if (purchaseOrderRepository.isDraft(orderId, PurchaseOrder.Status.DRAFT)) {
            purchaseOrderItemRepository.deleteById(itemId);

            PurchaseOrder order = purchaseOrderRepository.getReferenceById(orderId);
            recalculateOrderTotals(order);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBID0002");
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
    @Transactional(propagation = Propagation.MANDATORY)
    public void recalculateOrderTotals(PurchaseOrder order) {
        BigDecimal total = purchaseOrderItemRepository.calcOrderTotal(order.getId());
        order.setTotalAmount(total);

        BigDecimal paid = order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();
        BigDecimal debt = total.subtract(paid);
        if (debt.compareTo(BigDecimal.ZERO) < 0) {
            debt = BigDecimal.ZERO; // salomatlik uchun
        }
        order.setDebtAmount(debt);

        // paymentStatus ni ham avtomatik qo‘yamiz
        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            order.setPaymentStatus(PurchaseOrder.PaymentStatus.UNPAID);
        } else if (paid.compareTo(total) < 0) {
            order.setPaymentStatus(PurchaseOrder.PaymentStatus.PARTIAL);
        } else {
            order.setPaymentStatus(PurchaseOrder.PaymentStatus.PAID);
        }

        purchaseOrderRepository.save(order);
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
