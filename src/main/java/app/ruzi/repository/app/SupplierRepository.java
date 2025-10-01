package app.ruzi.repository.app;

import app.ruzi.entity.app.Supplier;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, String>, DataTablesRepository<Supplier, String> {
}