package app.ruzi.repository.tasks;

import app.ruzi.entity.tasks.Document;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String>, DataTablesRepository<Document, String> {

    @Modifying
    @Transactional
    void deleteByParentId(String privilegeId);

    List<Document> findByParentId(String privilegeId);

    long countAllByHash(String hash);
}