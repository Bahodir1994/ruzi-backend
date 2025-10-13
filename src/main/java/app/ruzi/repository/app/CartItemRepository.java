package app.ruzi.repository.app;

import app.ruzi.entity.app.CartItem;
import com.querydsl.core.Fetchable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String>, DataTablesRepository<CartItem, String> {
    List<CartItem> findByCartSession_IdOrderByInsTimeDesc(String sessionId);

    Optional<CartItem> findByCartSession_IdAndPurchaseOrderItem_Id(String id, String id1);
}