package app.ruzi.repository.app;

import app.ruzi.entity.app.Stock;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, String>, DataTablesRepository<Stock, String> {
}