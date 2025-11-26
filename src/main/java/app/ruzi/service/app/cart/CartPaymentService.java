package app.ruzi.service.app.cart;

import app.ruzi.entity.app.CartPayment;
import app.ruzi.entity.app.CartSession;
import app.ruzi.repository.app.CartPaymentRepository;
import app.ruzi.repository.app.CartSessionRepository;
import app.ruzi.service.payload.app.AddPaymentDto;
import app.ruzi.service.payload.app.AddPaymentResultDto;
import app.ruzi.service.payload.app.PaymentPartDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartPaymentService {

    private final CartPaymentRepository paymentRepo;
    private final CartSessionRepository cartSessionRepo;

    @Transactional(propagation = Propagation.MANDATORY)
    public PaymentSummary savePayments(CartSession cart, List<PaymentPartDto> parts) {

        BigDecimal paid = BigDecimal.ZERO;
        boolean hasCash = false;
        boolean hasCard = false;

        for (PaymentPartDto p : parts) {

            CartPayment cp = new CartPayment();
            cp.setCartSession(cart);
            cp.setCustomer(cart.getCustomer());
            cp.setMethod(CartPayment.Method.valueOf(p.getMethod()));
            cp.setAmount(p.getAmount());
            cp.setExternalTxnId(p.getExternalTxnId());

            paymentRepo.save(cp);

            paid = paid.add(p.getAmount());

            if ("CASH".equals(p.getMethod())) hasCash = true;
            if ("CARD".equals(p.getMethod())) hasCard = true;
        }

        return new PaymentSummary(paid, hasCash, hasCard);
    }

    @Getter
    @AllArgsConstructor
    public static class PaymentSummary {
        private BigDecimal paidAmount;
        private boolean hasCash;
        private boolean hasCard;
    }

    public java.util.List<CartPayment> getPayments(String cartSessionId) {
        return paymentRepo.findByCartSession_IdOrderByPaidAtAsc(cartSessionId);
    }
}

