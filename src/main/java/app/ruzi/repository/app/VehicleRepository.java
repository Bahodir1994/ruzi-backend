package app.ruzi.repository.app;

import app.ruzi.entity.app.Vehicle;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, DataTablesRepository<Vehicle, Long> {
}