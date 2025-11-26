package app.ruzi.service.payload.app;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddPaymentDto {

    private String cartSessionId;

    private List<PaymentPartDto> payments;

    @Data
    public static class PaymentPartDto {
        private String method;           // CASH, CARD
        private BigDecimal amount;
        private String externalTxnId;
    }
}
