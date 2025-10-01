package app.ruzi.repository.app;

import app.ruzi.entity.app.PurchaseOrderItem;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, String>, DataTablesRepository<PurchaseOrderItem, String> {
}