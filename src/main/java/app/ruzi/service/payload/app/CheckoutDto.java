package app.ruzi.service.payload.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckoutDto {
    private String cartSessionId;
    private List<PaymentPartDto> payments;
    private Integer referrerBonusPercent; // optional
}

