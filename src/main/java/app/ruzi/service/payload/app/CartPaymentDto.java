package app.ruzi.service.payload.app;

import app.ruzi.entity.app.CartPayment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DTO for {@link app.ruzi.entity.app.CartPayment}
 */
@AllArgsConstructor
@Getter
public class CartPaymentDto implements Serializable {
    private final String insUser;
    private final String updUser;
    private final Timestamp insTime;
    private final Timestamp updTime;
    private final Boolean isDeleted;
    private final String id;
    private final CartPayment.Method method;
    private final BigDecimal amount;
    private final String externalTxnId;
    private final LocalDateTime paidAt;
}