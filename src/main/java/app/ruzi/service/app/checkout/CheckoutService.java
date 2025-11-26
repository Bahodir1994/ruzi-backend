package app.ruzi.service.app.checkout;

import app.ruzi.entity.app.CartItem;
import app.ruzi.entity.app.CartSession;
import app.ruzi.repository.app.CartItemRepository;
import app.ruzi.repository.app.CartSessionRepository;
import app.ruzi.service.app.cart.CartPaymentService;
import app.ruzi.service.app.referrer.ReferrerService;
import app.ruzi.service.app.stock.StockService;
import app.ruzi.service.payload.app.CheckoutDto;
import app.ruzi.service.payload.app.CheckoutResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartSessionRepository cartRepo;
    private final CartItemRepository itemRepo;

    private final StockService stockService;
    private final CartPaymentService paymentService;
    private final ReferrerService referrerService;

    @Transactional
    public CheckoutResultDto checkout(CheckoutDto dto) {

        CartSession cart = cartRepo.findById(dto.getCartSessionId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getStatus() != CartSession.Status.OPEN)
            throw new IllegalStateException("Cart already closed or cancelled");

        // 1️⃣ Items → totalAmount
        List<CartItem> items = itemRepo.findByCartSession_IdOrderByInsTimeDesc(cart.getId());
        BigDecimal total = items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);

        // 2️⃣ Write-off stock
        for (CartItem item : items) {
            stockService.writeOffItem(item);
        }

        // 3️⃣ Payments
        CartPaymentService.PaymentSummary summary =
                paymentService.savePayments(cart, dto.getPayments());

        cart.setPaidAmount(summary.getPaidAmount());
        BigDecimal debt = total.subtract(summary.getPaidAmount());
        cart.setDebtAmount(debt.max(BigDecimal.ZERO));

        // paymentType
        if (summary.isHasCash() && summary.isHasCard())
            cart.setPaymentType(CartSession.PaymentType.MIXED);
        else if (summary.isHasCash())
            cart.setPaymentType(CartSession.PaymentType.CASH);
        else
            cart.setPaymentType(CartSession.PaymentType.CARD);

        // paymentStatus
        if (cart.getDebtAmount().compareTo(BigDecimal.ZERO) == 0)
            cart.setPaymentStatus(CartSession.PaymentStatus.PAID);
        else if (summary.getPaidAmount().compareTo(BigDecimal.ZERO) > 0)
            cart.setPaymentStatus(CartSession.PaymentStatus.PARTIAL);
        else
            cart.setPaymentStatus(CartSession.PaymentStatus.UNPAID);

        // 4️⃣ Referrer bonus
        referrerService.createBonus(cart, dto.getReferrerBonusPercent());

        // 5️⃣ Close session
        cart.setStatus(CartSession.Status.CHECKED_OUT);
        cart.setClosedAt(LocalDateTime.now());

        cartRepo.save(cart);

        return CheckoutResultDto.builder()
                .cartSessionId(cart.getId())
                .totalAmount(total)
                .paidAmount(cart.getPaidAmount())
                .debtAmount(cart.getDebtAmount())
                .paymentType(cart.getPaymentType().name())
                .paymentStatus(cart.getPaymentStatus().name())
                .build();
    }
}

