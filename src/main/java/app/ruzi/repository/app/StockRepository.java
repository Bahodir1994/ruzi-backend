package app.ruzi.repository.app;

import app.ruzi.entity.app.PurchaseOrderItem;
import app.ruzi.entity.app.Stock;
import app.ruzi.entity.app.Warehouse;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, String>, DataTablesRepository<Stock, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.purchaseOrderItem.id = :poiId")
    Optional<Stock> findByPurchaseOrderItem_Locked(
            @Param("poiId") String poiId,
            LockModeType lockMode
    );

    Optional<Stock> findByPurchaseOrderItemAndWarehouse(PurchaseOrderItem poi, Warehouse warehouse);
}