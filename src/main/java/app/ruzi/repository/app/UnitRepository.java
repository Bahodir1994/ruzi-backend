package app.ruzi.repository.app;

import app.ruzi.entity.app.Unit;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String>, DataTablesRepository<Unit, String> {
    Unit findByCode(String code);

    List<Unit> findAllByCodeIn(List<String> codes);

}