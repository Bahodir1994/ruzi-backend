package app.ruzi.repository.tasks;

import app.ruzi.entity.tasks.DocumentHash;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentHashRepository extends JpaRepository<DocumentHash, String>, DataTablesRepository<DocumentHash, String> {
    boolean existsByHash(String hash);
}