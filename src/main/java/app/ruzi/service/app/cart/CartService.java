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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public List<CartItemViewDto> getItemsBySessionId(String sessionId) {
        return cartItemRepository.findByCartSession_IdOrderByInsTimeDesc(sessionId)
                .stream()
                .map(item -> {
                    // Har bir item uchun tegishli stockni topamiz
                    var stockOpt = stockRepository.findByPurchaseOrderItemAndWarehouse(
                            item.getPurchaseOrderItem(), item.getWarehouse());

                    BigDecimal available = stockOpt
                            .map(s -> s.getQuantity().subtract(s.getReservedQuantity()))
                            .orElse(BigDecimal.ZERO);

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
                })
                .toList();
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

    /** yordamchi - cart uchun raqam generatsiyasi */
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

