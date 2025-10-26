package app.ruzi.repository.app;

import app.ruzi.entity.app.CartItem;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String>, DataTablesRepository<CartItem, String> {
    @EntityGraph(attributePaths = {
            "purchaseOrderItem",
            "purchaseOrderItem.item",
            "warehouse"
    })
    List<CartItem> findByCartSession_IdOrderByInsTimeDesc(String sessionId);

    Optional<CartItem> findByCartSession_IdAndPurchaseOrderItem_Id(String id, String id1);

    @EntityGraph(attributePaths = {
            "purchaseOrderItem",
            "purchaseOrderItem.item",
            "warehouse"
    })
    Optional<CartItem> findById(String id);

    @Query("""
                SELECT ci FROM CartItem ci
                JOIN FETCH ci.purchaseOrderItem poi
                JOIN FETCH poi.item it
                JOIN FETCH ci.warehouse w
                WHERE ci.cartSession.id = :cartSessionId
            """)
    List<CartItem> findAllByCartSessionIdWithRelations(@Param("cartSessionId") String cartSessionId);

    void deleteCartItemsByCartSession_Id(String cartSession_id);

}