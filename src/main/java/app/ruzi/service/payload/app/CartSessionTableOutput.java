package app.ruzi.service.payload.app;

import app.ruzi.entity.app.CartSession;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import lombok.Data;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class CartSessionTableOutput extends DataTablesOutput<CartSession> {
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
}

