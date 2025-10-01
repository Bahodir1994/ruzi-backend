package app.ruzi.repository.app;

import app.ruzi.entity.app.Warehouse;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String>, DataTablesRepository<Warehouse, String> {
}