package app.ruzi.repository.tasks;

import app.ruzi.entity.tasks.Document;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String>, DataTablesRepository<Document, String> {

    @Modifying
    @Transactional
    void deleteByParentId(String privilegeId);

    Page<Document> findAllByParentId(String parentId, Pageable pageable);

    @Modifying
    @Query("update Document set docName = ?3 where id = ?1 and parentId=?2")
    void updateByIdAndDocName(String id, String parentId, String name);

    List<Document> findByParentId(String privilegeId);

    long countAllByHash(String hash);
}