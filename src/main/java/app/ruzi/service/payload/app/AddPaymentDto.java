package app.ruzi.service.payload.app;


import app.ruzi.entity.app.CartSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddPaymentDto {
    private String cartSessionId;  // Savat sessiyasi ID
    private CartSession.PaymentType method;  // To‘lov usuli: CASH yoki CARD
    private BigDecimal amount;         // To‘lov miqdori
    private String externalTxnId;  // Agar bo‘lsa, tashqi tranzaksiya ID
    private Boolean loading;  // Agar bo‘lsa, tashqi tranzaksiya ID
}
