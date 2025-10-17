package app.ruzi.repository.app;

import app.ruzi.entity.app.Referrer;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferrerRepository extends JpaRepository<Referrer, String>, DataTablesRepository<Referrer, String> {
}