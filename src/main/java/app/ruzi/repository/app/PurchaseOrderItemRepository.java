package app.ruzi.repository.app;

import app.ruzi.entity.app.PurchaseOrderItem;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, String>, DataTablesRepository<PurchaseOrderItem, String> {

    @Query("select distinct poi.item.id from PurchaseOrderItem as poi where poi.item.id in (:idList)")
    List<String> findUsedItemIds(@Param("idList") List<String> idList);

    @Modifying
    @Query("delete from PurchaseOrderItem as p where p.id = :id")
    void deleteById(@Param("id") String id);

    @Modifying
    @Query("delete from PurchaseOrderItem as p where p.purchaseOrder.id = :orderId")
    void deleteByPurchaseOrder_Id(String orderId);

    @Query("select COALESCE(SUM(i.sum), 0) from PurchaseOrderItem i where i.purchaseOrder.id = :orderId")
    BigDecimal calcOrderTotal(@Param("orderId") String orderId);

}