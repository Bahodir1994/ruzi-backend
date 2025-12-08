package app.ruzi.service.payload.app;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CustomerStatsDto {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String tin;
    private String customerType;

    private Long cartCount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
}


