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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
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

    /** 1. Savatcha yaratish */
    public CartSession createSession(CreateCartDto createCartDto) {
        UserJwt userJwt = new UserJwt();
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        UserJwt finalUserJwt = userJwt;

        /* 1) Agar forceNew = true bo‘lsa — darhol yangi savatcha yaratiladi */
        if (createCartDto != null && Boolean.TRUE.equals(createCartDto.forceNew())) {
            return createNewSession(finalUserJwt);
        }

        /* 2) Agar activeSessionId keldi (localStorage orqali) */
        if (createCartDto != null && createCartDto.activeSessionId() != null) {
            CartSession existing = cartSessionRepository.findById(createCartDto.activeSessionId()).orElse(null);

            if (existing != null && existing.getStatus() == CartSession.Status.OPEN) {
                return existing;
            }
        }

        /* 3) Aks holda: kassirning eng so‘nggi OPEN sessiyasini qaytarish yoki yangisini yaratish */
        return cartSessionRepository.findFirstByInsUserAndStatusOrderByInsTimeDesc(
                userJwt.getUsername(),
                CartSession.Status.OPEN
        ).orElseGet(() -> createNewSession(finalUserJwt));
    }

    /** 2. Savatchaga tovar qo‘shish */
    @Transactional
    public void addItem(AddCartItemDto dto) {
        CartSession session = cartSessionRepository.findById(dto.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        PurchaseOrderItem poi = purchaseOrderItemRepository.findById(dto.purchaseOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException("PurchaseOrderItem not found"));

        /** 🔹 Zaxirani PESSIMISTIC lock bilan olamiz */
        Stock stock = stockRepository.findByPurchaseOrderItem_Locked(poi.getId(), LockModeType.PESSIMISTIC_WRITE)
                .orElseThrow(() -> new IllegalStateException("Stock not found"));

        /** 🔹 Savatda shu tovar bor-yo‘qligini tekshiramiz */
        Optional<CartItem> existingOpt = cartItemRepository
                .findByCartSession_IdAndPurchaseOrderItem_Id(session.getId(), poi.getId());

        BigDecimal quantityToAdd = dto.quantity();

        if (existingOpt.isPresent()) {
            /** 🔹 Agar bor bo‘lsa — mavjud miqdorga qo‘shamiz */
            CartItem existing = existingOpt.get();
            BigDecimal newQty = existing.getQuantity().add(quantityToAdd);

            BigDecimal available = stock.getQuantity().subtract(stock.getReservedQuantity());
            if (quantityToAdd.compareTo(available) > 0)
                throw new IllegalStateException("Not enough stock available");

            existing.setQuantity(newQty);
            existing.setLineTotal(existing.getUnitPrice().multiply(newQty));

            /** 🔹 Reservedni oshiramiz */
            stock.setReservedQuantity(stock.getReservedQuantity().add(quantityToAdd));
            cartItemRepository.save(existing);
        } else {
            /** 🔸 Yangi yozuv yaratamiz */
            BigDecimal available = stock.getQuantity().subtract(stock.getReservedQuantity());
            if (quantityToAdd.compareTo(available) > 0)
                throw new IllegalStateException("Not enough stock available");

            CartItem newItem = CartItem.builder()
                    .cartSession(session)
                    .purchaseOrderItem(poi)
                    .quantity(quantityToAdd)
                    .unitPrice(poi.getSalePrice())
                    .lineTotal(poi.getSalePrice().multiply(quantityToAdd))
                    .build();

            cartItemRepository.save(newItem);
            stock.setReservedQuantity(stock.getReservedQuantity().add(quantityToAdd));
        }

        stockRepository.save(stock);

        /** 🔹 WebSocket push — zaxira o‘zgardi */
        wsService.broadcastStockUpdate(toDto(stock));
    }

    /** Savatchadagi tovar sonini ozgartirish */
    public void updateItemQuantity(UpdateCartItemDto dto) {
        CartItem item = cartItemRepository.findById(dto.cartItemId())
                .orElseThrow(() -> new IllegalArgumentException("Cart item topilmadi"));

        Stock stock = stockRepository.findByPurchaseOrderItem_Locked(
                        item.getPurchaseOrderItem().getId(), LockModeType.PESSIMISTIC_WRITE)
                .orElseThrow(() -> new IllegalStateException("Stock topilmadi"));

        BigDecimal oldQty = item.getQuantity();
        BigDecimal newQty = dto.newQuantity();

        // 🔹 Farqni hisoblaymiz
        BigDecimal diff = newQty.subtract(oldQty);

        // 🔹 Agar orttirilsa — zaxiraga tekshiruv
        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal available = stock.getQuantity().subtract(stock.getReservedQuantity());
            if (diff.compareTo(available) > 0) {
                throw new IllegalStateException("Yetarli zaxira yo‘q");
            }
            stock.setReservedQuantity(stock.getReservedQuantity().add(diff));
        }
        // 🔹 Agar kamaytirilsa — rezervdan chiqaramiz
        else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            stock.setReservedQuantity(stock.getReservedQuantity().add(diff)); // diff manfiy
        }

        // 🔹 Yangilash
        item.setQuantity(newQty);
        item.setLineTotal(item.getUnitPrice().multiply(newQty));

        stockRepository.save(stock);
        cartItemRepository.save(item);

        wsService.broadcastStockUpdate(toDto(stock));
    }

    /** cart session boyicha itemlarni olish*/
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
            BigDecimal available = stock != null ? stock.getQuantity().subtract(stock.getReservedQuantity()) : BigDecimal.ZERO;
            return CartItemViewDto.builder()
                    .cartItemId(item.getId())
                    .purchaseOrderItemId(item.getPurchaseOrderItem().getId())
                    .itemName(item.getPurchaseOrderItem().getItem().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .lineTotal(item.getLineTotal())
                    .available(available)
                    .warehouseName(item.getWarehouse().getName())
                    .build();
        }).toList();
    }

    /** cartItem ni bittalab o'chirish*/
    @Modifying
    @Transactional
    public void deleteItem(String cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item topilmadi"));

        // 🔹 Tegishli stockni pessimistic lock bilan olamiz
        Stock stock = stockRepository.findByPurchaseOrderItem_Locked(
                        item.getPurchaseOrderItem().getId(), LockModeType.PESSIMISTIC_WRITE)
                .orElseThrow(() -> new IllegalStateException("Stock topilmadi"));

        // 🔹 ReservedQuantity ni kamaytiramiz
        BigDecimal reserved = stock.getReservedQuantity().subtract(item.getQuantity());
        if (reserved.compareTo(BigDecimal.ZERO) < 0) reserved = BigDecimal.ZERO;
        stock.setReservedQuantity(reserved);

        // 🔹 Saqlash
        stockRepository.save(stock);

        // 🔹 Itemni o‘chirish
        cartItemRepository.delete(item);

        // 🔹 WebSocket orqali yangilanishni yuboramiz
        wsService.broadcastStockUpdate(toDto(stock));
    }

    /** Savatcha boyicha undagi tovarlarni o'chirish */
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

                stock.setReservedQuantity(newReserved);
                stockRepository.save(stock);

                wsService.broadcastStockUpdate(toDto(stock));
            }
        }

        // 3️⃣ Itemlarni o‘chirish
        cartItemRepository.deleteCartItemsByCartSession_Id(cartSessionId);
    }

    /** Savatga Mijoz/Xamkor qoshish */
    @Transactional
    public void addCusRef(AddCustomerReferrerToCartDto dto) {
        CartSession session = cartSessionRepository.findById(dto.cardSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (dto.type().equals("CUSTOMER")){
            session.setCustomer(new Customer(dto.id()));
        }else {
            session.setCustomer(new Referrer(dto.id()));
        }
    }

    /** Savatchani ni bekor qilish */
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

                stock.setReservedQuantity(newReserved);
                stockRepository.save(stock);

                wsService.broadcastStockUpdate(toDto(stock));
            }
        }

        // 4️⃣ Cartning statusini CANCELLED qilib yangilaymiz
        session.setStatus(CartSession.Status.CANCELLED);
        cartSessionRepository.save(session);
    }

    /** yoradmchi - yangi cartSession chiqrish */
    private CartSession createNewSession(UserJwt userJwt) {
        CartSession session = CartSession.builder()
                .createdByUser(userJwt.getFullName())
                .status(CartSession.Status.OPEN)
                .cartNumber(generateCartNumber())
                .build();

        return cartSessionRepository.save(session);
    }

    /** yordamchi - savatcha uchun raqam generatsiyasi */
    private String generateCartNumber(){
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
    private StockViewDto toDto(Stock stock) {
        BigDecimal available = stock.getQuantity().subtract(stock.getReservedQuantity());
        return StockViewDto.builder()
                .stockId(stock.getId())
                .quantity(stock.getQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .availableQuantity(available)
                .itemName(stock.getPurchaseOrderItem().getItem().getName())
                .warehouseName(stock.getWarehouse().getName())
                .build();
    }
}

