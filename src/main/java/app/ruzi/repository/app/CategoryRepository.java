package app.ruzi.repository.app;

import app.ruzi.entity.app.Category;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String>, DataTablesRepository<Category, String> {

    @Query("select cat from Category as cat where cat.client.id = ?1 and cat.code = ?2")
    Optional<Category> findByQueryUniCat(String clientId, String code);

    @EntityGraph(attributePaths = {
            "client"
    })
    List<Category> findAllByIsDeleted(Boolean isDeleted);

}