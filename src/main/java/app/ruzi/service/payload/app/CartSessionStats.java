package app.ruzi.service.payload.app;

import java.math.BigDecimal;

public interface CartSessionStats {
    BigDecimal getTotalAmount();
    BigDecimal getPaidAmount();
    BigDecimal getDebtAmount();
}

