package app.ruzi.service.payload.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentPartDto {
    private String method;        // CASH | CARD
    private BigDecimal amount;
    private String externalTxnId; // nullable
}
