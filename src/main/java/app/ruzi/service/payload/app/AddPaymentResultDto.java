package app.ruzi.service.payload.app;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AddPaymentResultDto {

    private String cartSessionId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal newDebt;
    private String paymentStatus;  // UNPAID, PARTIAL, PAID
}
