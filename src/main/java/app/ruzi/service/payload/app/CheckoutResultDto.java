package app.ruzi.service.payload.app;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CheckoutResultDto {
    private String cartSessionId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
    private String paymentType;
    private String paymentStatus;
}
