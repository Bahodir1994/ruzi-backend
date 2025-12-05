package app.ruzi.service.payload.app;

import app.ruzi.entity.app.CartSession;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DTO for {@link app.ruzi.entity.app.CartSession}
 */
@Value
public class CartSessionDto implements Serializable {
    String insUser;
    String updUser;
    Timestamp insTime;
    Timestamp updTime;
    Boolean isDeleted;
    String id;
    String cartNumber;
    CartSession.PaymentType paymentType;
    CartSession.PaymentStatus paymentStatus;
    BigDecimal debtAmount;
    String createdByUser;
    CartSession.Status status;
    LocalDateTime createdAt;
    LocalDateTime closedAt;
    BigDecimal totalAmount;
    BigDecimal paidAmount;
}