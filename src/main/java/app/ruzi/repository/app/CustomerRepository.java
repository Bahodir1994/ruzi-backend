package app.ruzi.repository.app;

import app.ruzi.entity.app.Customer;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, DataTablesRepository<Customer, Long> {
}