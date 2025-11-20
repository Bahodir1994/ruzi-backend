package app.ruzi.repository.app;

import app.ruzi.entity.app.PurchaseOrder;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String>, DataTablesRepository<PurchaseOrder, String> {

    Optional<PurchaseOrder> findByInsUserAndStatus(String insUser, PurchaseOrder.Status status);

    @Query("select p.orderNumber from PurchaseOrder p " +
            "where p.client.id = :clientId and p.orderNumber like concat(:prefix, '%') order by p.orderNumber desc limit 1")
    String findLastByClientAndPrefix(String clientId, String prefix);

}
