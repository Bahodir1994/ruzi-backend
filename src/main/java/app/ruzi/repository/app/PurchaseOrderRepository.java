package app.ruzi.repository.app;

import app.ruzi.entity.app.PurchaseOrder;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String>, DataTablesRepository<PurchaseOrder, String> {

    Optional<PurchaseOrder> findByInsUserAndStatus(String insUser, PurchaseOrder.Status status);

    @Query("select p.orderNumber from PurchaseOrder p " +
            "where p.client.id = :clientId and p.orderNumber like concat(:prefix, '%') order by p.orderNumber desc limit 1")
    String findLastByClientAndPrefix(String clientId, String prefix);

    @Query("select case when count(p) > 0 " +
            "then true else false end from PurchaseOrder p " +
            "where p.id = :id and p.status = :status")
    boolean isDraft(@Param("id") String id, PurchaseOrder.Status status);

    @Modifying
    void deleteByIdAndStatus(String id, PurchaseOrder.Status status);

    boolean existsByIdAndStatus(String orderId, PurchaseOrder.Status status);
}
