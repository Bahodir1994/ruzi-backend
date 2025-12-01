package app.ruzi.service.app.cart;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.entity.app.*;
import app.ruzi.repository.app.CartItemRepository;
import app.ruzi.repository.app.CartSessionRepository;
import app.ruzi.repository.app.PurchaseOrderItemRepository;
import app.ruzi.repository.app.StockRepository;
import app.ruzi.service.app.stock.StockWebSocketService;
import app.ruzi.service.payload.app.*;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final JwtUtils jwtUtils;

    private final StockWebSocketService wsService;

    private final CartSessionRepository cartSessionRepository;
    private final CartItemRepository cartItemRepository;
    private final StockRepository stockRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    /**
     * 1. Savatcha yaratish
     */
    public CartSession createSession(CreateCartDto createCartDto) {
        UserJwt finalUserJwt = jwtUtils.extractUserFromToken();

        /* 1) Agar forceNew = true bo‘lsa — darhol yangi savatcha yaratiladi */
        if (createCartDto != null && Boolean.TRUE.equals(createCartDto.forceNew())) {
            return createNewSession(finalUserJwt);
        }

        /* 2) Agar activeSessionId keldi (localStorage orqali) */
        if (createCartDto != null && createCartDto.activeSessionId() != null) {
            CartSession existing = cartSessionRepository.findById(createCartDto.activeSessionId()).orElse(null);

            if (existing != null) {
                return existing;
            }
        }

        /* 3) Aks holda: kassirning eng so‘nggi OPEN sessiyasini qaytarish yoki yangisini yaratish */
        return cartSessionRepository.findFirstByInsUserAndStatusOrderByInsTimeDesc(
                finalUserJwt.getUsername(),
                CartSession.Status.OPEN
        ).orElseGet(() -> createNewSession(finalUserJwt));
    }

    /**
     * 2. Savatchaga tovar qo‘shish
     */
    @Transactional
    public void addItem(AddCartItemDto dto) {

        CartSession session = cartSessionRepository.findById(dto.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        PurchaseOrderItem poi = purchaseOrderItemRepository.findById(dto.purchaseOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException("PurchaseOrderItem not found"));

        // 🔒 Zaxira lock
        Stock stock = stockRepository.findByPurchaseOrderItem_Locked(
                poi.getId(), LockModeType.PESSIMISTIC_WRITE
        ).orElseThrow(() -> new IllegalStateException("Stock not found"));

        BigDecimal rate = poi.getConversionRate() != null ? poi.getConversionRate() : BigDecimal.ONE;

        // DTO miqdorlari
        BigDecimal pack = dto.packQuantity() != null ? dto.packQuantity() : BigDecimal.ZERO;
        BigDecimal alt = dto.altQuantity() != null ? dto.altQuantity() : BigDecimal.ZERO;

        // ALT ekvivalentni hisoblash
        BigDecimal totalAltEq = pack.multiply(rate).add(alt);

        // ❗ Zaxirada bormi? (FAQAT ALT hisobida tekshiramiz)
        BigDecimal availableAlt = stock.getAltQuantity()
                .subtract(stock.getReservedQuantity().multiply(rate))
                .subtract(stock.getReservedAltQuantity());

        if (totalAltEq.compareTo(availableAlt) > 0) {
            throw new IllegalStateException("Yetarli zaxira yo‘q");
        }

        // ===== CART ITEM BORMI? =====
        Optional<CartItem> opt = cartItemRepository
                .findByCartSession_IdAndPurchaseOrderItem_Id(session.getId(), poi.getId());

        if (opt.isPresent()) {

            CartItem item = opt.get();

            item.setQuantity(item.getQuantity().add(pack));
            item.setAltQuantity(item.getAltQuantity().add(alt));
            item.setTotalAltQuantity(item.getTotalAltQuantity().add(totalAltEq));

            // Yangi umumiy pack ekvivalent
            BigDecimal newPackEq = item.getQuantity()
                    .add(item.getAltQuantity().divide(rate, 6, RoundingMode.HALF_UP));

            item.setLineTotal(item.getUnitPrice().multiply(newPackEq));

            cartItemRepository.save(item);
        }
        else {

            BigDecimal price = poi.getSalePrice();

            CartItem item = CartItem.builder()
                    .cartSession(session)
                    .purchaseOrderItem(poi)
                    .warehouse(stock.getWarehouse())

                    .quantity(pack)
                    .altQuantity(alt)
                    .totalAltQuantity(totalAltEq)

                    .unitPrice(price)
                    .lineTotal(price.multiply(
                            pack.add(alt.divide(rate, 6, RoundingMode.HALF_UP))
                    ))
                    .build();

            cartItemRepository.save(item);
        }

        // === STOCK REZERVNI YANGILAYMIZ ===
        stock.setReservedQuantity( stock.getReservedQuantity().add(pack) );
        stock.setReservedAltQuantity( stock.getReservedAltQuantity().add(alt) );

        stockRepository.save(stock);

        wsService.broadcastStockUpdate(toDto(stock));
    }


    /**
     * Savatchadagi tovar miqdorini yangilash (asosiy + alt)
     */
    @Transactional
    public void updateItemQuantity(UpdateCartItemDto dto) {

        CartItem item = cartItemRepository.findById(dto.cartItemId())
                .orElseThrow(() -> new IllegalArgumentException("Cart item topilmadi"));

        PurchaseOrderItem poi = item.getPurchaseOrderItem();
        Stock stock = stockRepository.findByPurchaseOrderItem_Locked(
                        poi.getId(), LockModeType.PESSIMISTIC_WRITE)
                .orElseThrow(() -> new IllegalStateException("Stock topilmadi"));

        BigDecimal rate = poi.getConversionRate() != null ? poi.getConversionRate() : BigDecimal.ONE;

        // Eski qiymatlar
        BigDecimal oldPack = item.getQuantity();
        BigDecimal oldAlt  = item.getAltQuantity() != null ? item.getAltQuantity() : BigDecimal.ZERO;

        // Yangi qiymatlar
        BigDecimal newPack = dto.newQuantity();
        BigDecimal newAlt  = dto.newAltQuantity() != null ? dto.newAltQuantity() : BigDecimal.ZERO;

        // ==== FARQLAR ====
        BigDecimal diffPack = newPack.subtract(oldPack);   // PACK farqi
        BigDecimal diffAlt  = newAlt.subtract(oldAlt);     // ALT farqi

        // === 1) ZAXIRA TEKSHIRISH ===

        // pack farqidan keladigan ALT ekvivalent (zaxira tekshirish uchun)
        BigDecimal packDiffAsAlt = diffPack.multiply(rate);

        // jami ALT ekvivalent farqi
        BigDecimal totalAltDiffEq = packDiffAsAlt.add(diffAlt);

        // mavjud ALT
        BigDecimal availableAlt = stock.getAltQuantity()
                .subtract(stock.getReservedQuantity().multiply(rate))
                .subtract(stock.getReservedAltQuantity());

        if (totalAltDiffEq.compareTo(BigDecimal.ZERO) > 0) {
            // ALT ekvivalentda ko‘payayotgan bo‘lsa — stock yetadimi?
            if (totalAltDiffEq.compareTo(availableAlt) > 0) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBID0003");
            }
        }

        // === 2) REZERV YOZISH ===

        // PACK rezervni o‘zgartiramiz
        stock.setReservedQuantity(
                stock.getReservedQuantity().add(diffPack)
        );

        // ALT rezervni o‘zgartiramiz (faqat alt qismi!)
        stock.setReservedAltQuantity(
                stock.getReservedAltQuantity().add(diffAlt)
        );

        // === 3) CART ITEM YANGILASH ===
        item.setQuantity(newPack);
        item.setAltQuantity(newAlt);

        // ⭐️ Yangi total ALT ekvivalent (cart item uchun)
        BigDecimal totalAltEq = newPack.multiply(rate).add(newAlt);
        item.setTotalAltQuantity(totalAltEq);

        // Jami pack ekvivalent (chiqim summasi uchun)
        BigDecimal totalPackEq = newPack.add(
                newAlt.divide(rate, 6, RoundingMode.HALF_UP)
        );

        item.setLineTotal(
                item.getUnitPrice().multiply(totalPackEq)
        );

        // save
        stockRepository.save(stock);
        cartItemRepository.save(item);

        wsService.broadcastStockUpdate(toDto(stock));
    }



    @Transactional
    public void updateItemPrice(UpdateCartItemPriceDto dto) {
        CartItem item = cartItemRepository.findById(dto.cartItemId())
                .orElseThrow(() -> new IllegalArgumentException("Cart item topilmadi"));

        BigDecimal newPrice = dto.newPrice();
        BigDecimal discountPercent = dto.discountPercent() != null ? dto.discountPercent() : BigDecimal.ZERO;

        // 🔹 Chegirma summasi
        BigDecimal discountAmount = newPrice.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        BigDecimal finalPrice = newPrice.subtract(discountAmount);

        // 🔹 CartItemni yangilaymiz
        item.setUnitPrice(finalPrice);
        item.setDiscount(discountAmount);
        item.setLineTotal(finalPrice.multiply(item.getQuantity()));

        cartItemRepository.save(item);
    }


    /**
     * cart session boyicha itemlarni olish
     */
    @Transactional(readOnly = true)
    public List<CartItemViewDto> getItemsBySessionId(String sessionId) {
        var items = cartItemRepository.findByCartSession_IdOrderByInsTimeDesc(sessionId);

        // Stocklarni bulk query orqali olish
        var poiIds = items.stream().map(i -> i.getPurchaseOrderItem().getId()).toList();
        var whIds = items.stream().map(i -> i.getWarehouse().getId()).toList();

        var stockList = stockRepository.findAllByPurchaseOrderItemIdInAndWarehouseIdIn(poiIds, whIds);
        var stockMap = stockList.stream().collect(Collectors.toMap(
                s -> s.getPurchaseOrderItem().getId() + "_" + s.getWarehouse().getId(),
                s -> s
        ));

        return items.stream().map(item -> {
            var key = item.getPurchaseOrderItem().getId() + "_" + item.getWarehouse().getId();
            var stock = stockMap.get(key);
            var poi = item.getPurchaseOrderItem();

            BigDecimal available = stock != null
                    ? stock.getQuantity().subtract(stock.getReservedQuantity())
                    : BigDecimal.ZERO;

            BigDecimal availableAlt = stock != null
                    ? stock.getAltQuantity().subtract(stock.getReservedAltQuantity())
                    : BigDecimal.ZERO;

            return CartItemViewDto.builder()
                    .cartItemId(item.getId())
                    .purchaseOrderItemId(poi.getId())
                    .itemName(poi.getItem().getName())
                    .quantity(item.getQuantity())
                    .altQuantity(item.getAltQuantity())
                    .unitPrice(item.getUnitPrice())
                    .lineTotal(item.getLineTotal())
                    .available(available)
                    .availableAlt(availableAlt)                // 🆕 alt birlikdagi mavjud miqdor
                    .conversionRate(poi.getConversionRate())   // 🆕 1 pack = N pcs
                    .warehouseName(item.getWarehouse().getName())

                    // 🔽 PurchaseOrderItem’dan
                    .unitCode(poi.getUnitCode())
                    .altUnitCode(poi.getAltUnitCode())
                    .packageCount(poi.getPackageCount())
                    .salePrice(poi.getSalePrice())
                    .altSalePrice(poi.getAltSalePrice())
                    .purchasePrice(poi.getPurchasePrice())
                    .minimalSum(poi.getMinimalSum())
                    .purchaseDiscount(poi.getDiscount())

                    // 🔽 CartItem’dan (bizning kassadagi chegirma)
                    .saleDiscount(item.getDiscount())
                    .build();
        }).toList();
    }


    /**
     * cartItem ni bittalab o'chirish
     */
    @Modifying
    @Transactional
    public void deleteItem(String cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item topilmadi"));

        PurchaseOrderItem poi = item.getPurchaseOrderItem();
        BigDecimal rate = poi.getConversionRate() != null ? poi.getConversionRate() : BigDecimal.ONE;

        Stock stock = stockRepository.findByPurchaseOrderItem_Locked(
                poi.getId(), LockModeType.PESSIMISTIC_WRITE
        ).orElseThrow(() -> new IllegalStateException("Stock topilmadi"));

        // PACK rezervni kamaytirish
        BigDecimal newPackRes = stock.getReservedQuantity().subtract(item.getQuantity());
        if (newPackRes.compareTo(BigDecimal.ZERO) < 0) newPackRes = BigDecimal.ZERO;
        stock.setReservedQuantity(newPackRes);

        // ALT rezervni kamaytirish — FAQAT item.altQuantity!
        BigDecimal newAltRes = stock.getReservedAltQuantity()
                .subtract(item.getAltQuantity() != null ? item.getAltQuantity() : BigDecimal.ZERO);

        if (newAltRes.compareTo(BigDecimal.ZERO) < 0) newAltRes = BigDecimal.ZERO;

        stock.setReservedAltQuantity(newAltRes);

        stockRepository.save(stock);

        cartItemRepository.delete(item);

        wsService.broadcastStockUpdate(toDto(stock));
    }


    /**
     * Savatcha boyicha undagi tovarlarni o'chirish
     */
    @Modifying
    @Transactional
    public void deleteCart(String cartSessionId) {
        // 1️⃣ Barcha cart itemlarni bir yo‘la olish (fetch bilan)
        List<CartItem> items = cartItemRepository.findAllByCartSessionIdWithRelations(cartSessionId);

        if (items.isEmpty()) {
            // agar bo‘sh bo‘lsa, faqat sessiyani o‘chir
            cartSessionRepository.deleteById(cartSessionId);
            return;
        }

        // 2️⃣ Har bir item uchun stock ni lock qilib, reservedQuantity ni kamaytirish
        for (CartItem item : items) {
            String purchaseOrderItemId = item.getPurchaseOrderItem().getId();
            String warehouseId = item.getWarehouse().getId();

            Stock stock = stockRepository.findByPurchaseOrderItemAndWarehouse_Locked(
                    purchaseOrderItemId, warehouseId, LockModeType.PESSIMISTIC_WRITE
            ).orElse(null);

            if (stock != null) {
                BigDecimal newReserved = stock.getReservedQuantity().subtract(item.getQuantity());
                if (newReserved.compareTo(BigDecimal.ZERO) < 0)
                    newReserved = BigDecimal.ZERO;

                BigDecimal reservedAlt = stock.getReservedAltQuantity()
                        .subtract(item.getQuantity().multiply(item.getPurchaseOrderItem().getConversionRate()));
                if (reservedAlt.compareTo(BigDecimal.ZERO) < 0) reservedAlt = BigDecimal.ZERO;
                stock.setReservedAltQuantity(reservedAlt);


                stock.setReservedQuantity(newReserved);
                stockRepository.save(stock);

                wsService.broadcastStockUpdate(toDto(stock));
            }
        }

        // 3️⃣ Itemlarni o‘chirish
        cartItemRepository.deleteCartItemsByCartSession_Id(cartSessionId);
    }

    /**
     * Savatga Mijoz/Xamkor qoshish
     */
    @Transactional
    public void addCusRef(AddCustomerReferrerToCartDto dto) {
        CartSession session = cartSessionRepository.findById(dto.cartSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (dto.type().equals("CUSTOMER")) {
            session.setCustomer(new Customer(dto.id()));
        } else {
            session.setReferrer(new Referrer(dto.id()));
        }

        cartSessionRepository.save(session);
    }

    /**
     * Savatdan Mijoz/Xamkor larni ochirish
     */
    @Transactional
    public void removeCusRef(RemoveCustomerReferrerToCartDto dto) {
        CartSession session = cartSessionRepository.findById(dto.cartSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        switch (dto.type()) {
            case "CUSTOMER" -> session.setCustomer(null);
            case "REFERRER" -> session.setReferrer(null);
            default -> throw new IllegalArgumentException("type must be CUSTOMER or REFERRER");
        }
        cartSessionRepository.save(session);
    }

    /**
     * Savatchani ni bekor qilish
     */
    @Transactional
    public void cancelCart(String cartSessionId) {
        // 1️⃣ CartSession ni topamiz
        CartSession session = cartSessionRepository.findById(cartSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Cart session topilmadi"));

        // 2️⃣ Faqat OPEN holatdagilarni bekor qilamiz
        if (session.getStatus() != CartSession.Status.OPEN) {
            throw new IllegalStateException("Faqat OPEN holatdagi savatchani bekor qilish mumkin");
        }

        // 3️⃣ Barcha itemlarni fetch bilan olamiz
        List<CartItem> items = cartItemRepository.findAllByCartSessionIdWithRelations(cartSessionId);

        for (CartItem item : items) {
            String purchaseOrderItemId = item.getPurchaseOrderItem().getId();
            String warehouseId = item.getWarehouse().getId();

            Stock stock = stockRepository.findByPurchaseOrderItemAndWarehouse_Locked(
                    purchaseOrderItemId, warehouseId, LockModeType.PESSIMISTIC_WRITE
            ).orElse(null);

            if (stock != null) {
                BigDecimal newReserved = stock.getReservedQuantity().subtract(item.getQuantity());
                if (newReserved.compareTo(BigDecimal.ZERO) < 0)
                    newReserved = BigDecimal.ZERO;

                BigDecimal reservedAlt = stock.getReservedAltQuantity()
                        .subtract(item.getQuantity().multiply(item.getPurchaseOrderItem().getConversionRate()));
                if (reservedAlt.compareTo(BigDecimal.ZERO) < 0) reservedAlt = BigDecimal.ZERO;
                stock.setReservedAltQuantity(reservedAlt);


                stock.setReservedQuantity(newReserved);
                stockRepository.save(stock);

                wsService.broadcastStockUpdate(toDto(stock));
            }
        }

        // 4️⃣ Cartning statusini CANCELLED qilib yangilaymiz
        session.setStatus(CartSession.Status.CANCELLED);
        cartSessionRepository.save(session);
    }

    /**
     * yoradmchi - yangi cartSession chiqrish
     */
    private CartSession createNewSession(UserJwt userJwt) {
        CartSession session = CartSession.builder()
                .createdByUser(userJwt.getFullName())
                .status(CartSession.Status.OPEN)
                .cartNumber(generateCartNumber())
                .build();

        return cartSessionRepository.save(session);
    }

    /**
     * yordamchi - savatcha uchun raqam generatsiyasi
     */
    private String generateCartNumber() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String prefix = "ST" + datePart;

        // 🔹 Eng oxirgi savat raqamini topamiz
        String lastNumber = cartSessionRepository.findLastCartNumberForDate(prefix + "%");

        long nextNum = 1L;
        if (lastNumber != null) {
            String[] parts = lastNumber.split("-");
            nextNum = Long.parseLong(parts[1]) + 1;
        }

        return String.format("%s-%05d", prefix, nextNum);
    }

    /** yordamchi - natijani dto ga set qilish */
    /**
     * yordamchi - natijani dto ga set qilish
     */
    private StockViewDto toDto(Stock stock) {

        BigDecimal rate = stock.getPurchaseOrderItem().getConversionRate();

        // AVAILABLE ALT = altQuantity - (reservedPack * rate) - reservedAlt
        BigDecimal availableAlt = stock.getAltQuantity()
                .subtract(stock.getReservedQuantity().multiply(rate))
                .subtract(stock.getReservedAltQuantity());

        // AVAILABLE PACK = quantity - reservedQuantity
        BigDecimal availablePack = stock.getQuantity()
                .subtract(stock.getReservedQuantity());

        return StockViewDto.builder()
                .stockId(stock.getId())

                .quantity(stock.getQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .availableQuantity(availablePack)

                .altQuantity(stock.getAltQuantity())
                .reservedAltQuantity(stock.getReservedAltQuantity())
                .availableAltQuantity(availableAlt)

                .conversionRate(rate)

                .itemName(stock.getPurchaseOrderItem().getItem().getName())
                .warehouseName(stock.getWarehouse().getName())
                .build();
    }


    @Transactional(readOnly = true)
    public DataTablesOutput<CartSession> readTableCart(DataTablesInput input) {
        Specification<CartSession> spec = (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));

        return cartSessionRepository.findAll(input, spec);
    }

}

