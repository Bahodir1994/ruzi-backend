package app.ruzi.repository;

import app.ruzi.entity.PurchaseOrder;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, DataTablesRepository<PurchaseOrder, Long> {
}
