package app.ruzi.repository.app;

import app.ruzi.entity.app.Warehouse;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String>, DataTablesRepository<Warehouse, String> {

    List<Warehouse> findAllByClient_Id(String clientId);
}