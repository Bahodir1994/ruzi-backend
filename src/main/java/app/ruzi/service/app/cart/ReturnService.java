package app.ruzi.service.app.cart;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.entity.app.*;
import app.ruzi.repository.app.*;
import app.ruzi.service.payload.app.ReturnLineDto;
import app.ruzi.service.payload.app.ReturnRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final CartSessionRepository cartSessionRepository;
    private final CartItemRepository cartItemRepository;
    private final ReturnOrderRepository returnOrderRepository;
    private final ReturnItemRepository returnItemRepository;
    private final StockRepository stockRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public ReturnOrder createReturn(ReturnRequest request) {
        UserJwt userJwt = jwtUtils.extractUserFromToken();


        // 1) CART SESSION
        CartSession cart = cartSessionRepository.findById(request.getCartSessionId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR1001")
                );

        // 2) Faqat CHECKED_OUT bo‘lsa
        if (!CartSession.Status.CHECKED_OUT.equals(cart.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR1002");
        }

        // 3) Return Order yaratish
        ReturnOrder order = ReturnOrder.builder()
                .cartSession(cart)
                .reason(request.getReason())
                .employee(userJwt.getFullName())
                .createdAt(LocalDateTime.now())
                .totalReturnAmount(BigDecimal.ZERO)
                .build();

        returnOrderRepository.save(order);

        BigDecimal totalReturn = BigDecimal.ZERO;

        // 4) har bir qaytarilayotgan itemni qayta ishlash
        for (ReturnLineDto line : request.getItems()) {

            CartItem cartItem = cartItemRepository.findById(line.getCartItemId())
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR1003")
                    );

            BigDecimal returnQty = BigDecimal.valueOf(line.getQuantity());

            // validation
            if (returnQty.compareTo(BigDecimal.ZERO) <= 0 ||
                    cartItem.getQuantity().compareTo(returnQty) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR1004");
            }

            // PO Item (partiya)
            PurchaseOrderItem poi = cartItem.getPurchaseOrderItem();

            // Warehouse stock
            Stock stock = stockRepository
                    .findByPurchaseOrderItemIdAndWarehouseId_Locked(
                            poi.getId(),
                            cartItem.getWarehouse().getId()
                    )
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR1005")
                    );

            BigDecimal unitPrice = cartItem.getUnitPrice();
            BigDecimal lineTotal = unitPrice.multiply(returnQty);
            totalReturn = totalReturn.add(lineTotal);

            // ReturnItem yaratish
            ReturnItem ri = ReturnItem.builder()
                    .returnOrder(order)
                    .purchaseOrderItem(poi)   // ITEM emas → PARTIYA bilan ishlaymiz
                    .item(poi.getItem())
                    .unitPrice(unitPrice)
                    .quantity(returnQty)
                    .lineTotal(lineTotal)
                    .build();

            returnItemRepository.save(ri);

            //♦♦♦ STOCK QAYTARILADI ♦♦♦
            stock.setQuantity(stock.getQuantity().add(returnQty));

            if (poi.getConversionRate() != null &&
                    poi.getConversionRate().compareTo(BigDecimal.ZERO) > 0) {

                stock.setAltQuantity(
                        stock.getAltQuantity().add(returnQty.multiply(poi.getConversionRate()))
                );
            }

            stockRepository.save(stock);

            //♦♦♦ PURCHASE ORDER ITEM QAYTARILADI ♦♦♦
            poi.setQuantity(poi.getQuantity().add(returnQty));
            purchaseOrderItemRepository.save(poi);
        }

        order.setTotalReturnAmount(totalReturn);
        return returnOrderRepository.save(order);
    }
}
