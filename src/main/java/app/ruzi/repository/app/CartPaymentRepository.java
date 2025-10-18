package app.ruzi.repository.app;

import app.ruzi.entity.app.CartPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartPaymentRepository extends JpaRepository<CartPayment, String> {
    List<CartPayment> findByCartSession_IdOrderByPaidAtAsc(String cartSessionId);
//    BigDecimalSumProjection sumByCart(String cartSessionId); // ixtiyoriy aggregatsiya uchun
}
