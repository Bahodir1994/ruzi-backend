package app.ruzi.repository.app;

import app.ruzi.entity.app.CartPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartPaymentRepository extends JpaRepository<CartPayment, String> {
    List<CartPayment> findByCartSession_IdOrderByPaidAtAsc(String cartSessionId);
//    BigDecimalSumProjection sumByCart(String cartSessionId); // ixtiyoriy aggregatsiya uchun
}
