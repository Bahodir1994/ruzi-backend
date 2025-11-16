package app.ruzi.repository.app;

import app.ruzi.entity.app.Item;
import app.ruzi.entity.app.PurchaseOrderItem;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, String>, DataTablesRepository<PurchaseOrderItem, String> {

    @Query("select distinct poi.item.id from PurchaseOrderItem as poi where poi.item.id in (:idList)")
    List<String> findUsedItemIds(@Param("idList") List<String> idList);
}