package app.ruzi.repository.app;

import app.ruzi.entity.app.Customer;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String>, DataTablesRepository<Customer, String> {

    Optional<Customer> findById(String id);
}